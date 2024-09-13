package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager

@Component
class PhasesTypeValidator : AbstractSchemeValidator(Level.FOURTH) {

    override fun validate(scheme: SchemeDomain): Either<SchemeContainsLinksWithInvalidPhasesConnection, Unit> {
        val portIdToPortDtoMap = scheme.nodes.values.flatMap { it.ports }.associateBy { it.id }
        val equipmentIdToEquipmentNodeDtoMap = scheme.nodes.values.associateBy { it.id }

        scheme.links.values.forEach { link ->
            val sourceEquipment = equipmentIdToEquipmentNodeDtoMap[link.source]!!
            val targetEquipment = equipmentIdToEquipmentNodeDtoMap[link.target]!!
            val sourcePort = portIdToPortDtoMap[link.sourcePort]!!
            val targetPort = portIdToPortDtoMap[link.targetPort]!!

            val sourcePortPhase = EquipmentMetaInfoManager.getPortPhasesByEquipmentLibIdAndPortLibId(
                sourceEquipment.libEquipmentId,
                sourcePort.libId
            )
            val targetPortPhase = EquipmentMetaInfoManager.getPortPhasesByEquipmentLibIdAndPortLibId(
                targetEquipment.libEquipmentId,
                targetPort.libId
            )

            if (sourcePortPhase != targetPortPhase) {
                return SchemeContainsLinksWithInvalidPhasesConnection(sourceEquipment.id).left()
            }
        }
        return Unit.right()
    }
}

class SchemeContainsLinksWithInvalidPhasesConnection(
    val equipmentId: String
) : ValidationError
