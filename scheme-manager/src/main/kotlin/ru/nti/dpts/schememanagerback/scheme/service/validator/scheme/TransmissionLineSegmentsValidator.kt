package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.BreadthFirstSearch
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.EquipmentNodePredicate
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getTransmissionLineIds
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getVoltageInKilovolts
import ru.nti.dpts.schememanagerback.scheme.service.ops.findNearestVoltageLevelByVoltageInKilovolts
import ru.nti.dpts.schememanagerback.scheme.usecases.scenarious.NO_ID_TRANSMISSION_LINE
import ru.nti.dtps.equipment.meta.info.extension.isTransmissionLine
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

@Component
class TransmissionLineSegmentsValidator : AbstractSchemeValidator(Level.FOURTH) {

    override fun validate(scheme: SchemeDomain): Either<TransmissionLineValidationError, Unit> {
        return validateTransmissionLineSegments(scheme)
    }

    private fun validateTransmissionLineSegments(scheme: SchemeDomain): Either<TransmissionLineValidationError, Unit> {
        val transmissionLineIdToSegmentsMap = getTransmissionLineIdToSegmentsMap(scheme)
        transmissionLineIdToSegmentsMap.forEach { (transmissionLineId, lineSegments) ->
            if (transmissionLineId != NO_ID_TRANSMISSION_LINE && lineSegments.size > 1) {
                checkIdenticalVoltageLevelsForSegments(transmissionLineId, lineSegments, scheme).mapLeft { return it.left() }
                checkPortsConnectionBetweenSegments(transmissionLineId, lineSegments, scheme).mapLeft { return it.left() }
            }
        }
        return Unit.right()
    }

    private fun checkIdenticalVoltageLevelsForSegments(
        transmissionLineId: String,
        transmissionLineSegments: Set<EquipmentNodeDomain>,
        scheme: SchemeDomain
    ): Either<TransmissionLineValidationError.TransmissionLineHasSegmentWithDifferentVoltages, Unit> {
        val voltageLevelsOfSegments = transmissionLineSegments
            .map { findNearestVoltageLevelByVoltageInKilovolts(it.getVoltageInKilovolts()) }
            .toSet()
        if (voltageLevelsOfSegments.size > 1) {
            return TransmissionLineValidationError.TransmissionLineHasSegmentWithDifferentVoltages(
                scheme.transmissionLines.first { it.id == transmissionLineId }.name
            ).left()
        }
        return Unit.right()
    }

    private fun checkPortsConnectionBetweenSegments(
        transmissionLineId: String,
        segments: Set<EquipmentNodeDomain>,
        scheme: SchemeDomain
    ): Either<TransmissionLineValidationError.TransmissionLineSegmentsDoesNotConnectedError, Unit> {
        val segmentsIds = segments.map { segment -> segment.id }.toSet()
        val boundPredicate: EquipmentNodePredicate = { node ->
            when (node.libEquipmentId) {
                EquipmentLibId.CONNECTIVITY -> false
                EquipmentLibId.CURRENT_TRANSFORMER -> false
                EquipmentLibId.VOLTAGE_TRANSFORMER -> false
                EquipmentLibId.TRANSMISSION_LINE_SEGMENT -> !segmentsIds.contains(node.id)
                EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT -> !segmentsIds.contains(node.id)
                else -> true
            }
        }
        val bfs = BreadthFirstSearch(scheme, boundPredicate)

        segments.first().also { segment ->
            val connectedSegments = mutableSetOf(segment).also { connectedSegments ->
                bfs.searchFromNode(segment) { sibling, _, _ ->
                    if (segmentsIds.contains(sibling.id)) {
                        connectedSegments.add(sibling)
                    }
                }
            }

            val disconnectedSegments = connectedSegments.map { node -> node.id }.toSet().let {
                segmentsIds.minus(it)
            }
            if (disconnectedSegments.isNotEmpty()) {
                return TransmissionLineValidationError.TransmissionLineSegmentsDoesNotConnectedError(
                    scheme.transmissionLines.first { it.id == transmissionLineId }.name
                ).left()
            }
        }
        return Unit.right()
    }

    private fun getTransmissionLineIdToSegmentsMap(scheme: SchemeDomain): MutableMap<String, MutableSet<EquipmentNodeDomain>> {
        val transmissionLineIdToSegmentsMap: MutableMap<String, MutableSet<EquipmentNodeDomain>> = mutableMapOf()

        scheme.nodes.values
            .filter {
                it.libEquipmentId.isTransmissionLine() ||
                    it.libEquipmentId == EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT
            }
            .forEach { transmissionLineSegment ->
                transmissionLineSegment.getTransmissionLineIds()
                    .forEach { transmissionLineId ->
                        val segmentsByTransmissionLineId = transmissionLineIdToSegmentsMap[transmissionLineId]
                        if (segmentsByTransmissionLineId != null) {
                            segmentsByTransmissionLineId.add(transmissionLineSegment)
                        } else {
                            transmissionLineIdToSegmentsMap[transmissionLineId] =
                                mutableSetOf(transmissionLineSegment)
                        }
                    }
            }

        return transmissionLineIdToSegmentsMap
    }
}

sealed class TransmissionLineValidationError : ValidationError {
    class TransmissionLineSegmentsDoesNotConnectedError(val transmissionLineName: String) : TransmissionLineValidationError()
    class TransmissionLineHasSegmentWithDifferentVoltages(val transmissionLineName: String) : TransmissionLineValidationError()
}
