package ru.nti.dtps.cimconverter.fromcim.extractor.reducer

import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.rdf.schema.AbstractCimClass
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.mutable.MutableRawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.mutable.MutableRawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

object SchemeReducer {

    fun reduce(
        repository: RdfRepository,
        scheme: RawSchemeDto
    ): RawSchemeDto = reduce(mark(repository), scheme.toMutable()).toImmutable()

    private fun mark(repository: RdfRepository): List<String> {
        val disconnectorIds = markEquipmentIfAmountExceed(
            repository,
            CimClasses.Disconnector,
            200
        )

        val groundDisconnectorIds = markEquipmentIfAmountExceed(
            repository,
            CimClasses.GroundDisconnector,
            200
        )

        val jumperIds = markEquipmentIfAmountExceed(
            repository,
            CimClasses.Jumper,
            1
        )

        val potentialTransformerIds = markEquipmentIfAmountExceed(
            repository,
            CimClasses.PotentialTransformer,
            200
        )

        val breakerIds = markEquipmentIfAmountExceed(
            repository,
            CimClasses.Breaker,
            200
        )

        return disconnectorIds + groundDisconnectorIds + jumperIds + potentialTransformerIds + breakerIds
    }

    private fun markEquipmentIfAmountExceed(
        repository: RdfRepository,
        concreteEquipmentClass: AbstractCimClass,
        maxAllowableEquipmentAmount: Int
    ): List<String> {
        val concreteEquipmentIds = repository.selectAllVarsFromTriples(
            subject = concreteEquipmentClass,
            CimClasses.IdentifiedObject.name
        ).mapIndexed { _, bindingSet -> bindingSet.extractIdentifiedObjectId() }

        return if (concreteEquipmentIds.size >= maxAllowableEquipmentAmount) {
            concreteEquipmentIds
        } else {
            emptyList()
        }
    }

    private fun reduce(
        equipmentIdsToRemove: List<String>,
        mutableScheme: MutableRawSchemeDto
    ): MutableRawSchemeDto {
        val equipmentsToRemove = mutableScheme.nodes.filterKeys(equipmentIdsToRemove::contains)

        equipmentsToRemove.values.forEach { equipmentToRemove ->
            when (equipmentToRemove.ports.size) {
                1 -> removeEquipmentWithOnePortFromScheme(
                    equipmentToRemove,
                    mutableScheme
                )

                2 -> removeEquipmentWithTwoPortsFromScheme(
                    equipmentToRemove,
                    mutableScheme
                )
            }
        }

        return mutableScheme
    }

    private fun removeEquipmentWithOnePortFromScheme(
        removingEquipment: MutableRawEquipmentNodeDto,
        mutableScheme: MutableRawSchemeDto
    ) {
        val removingLinkId = removingEquipment.ports.first().links.first()

        val link = mutableScheme.links[removingLinkId]!!
        val sourcePort = link.getSourcePort(mutableScheme)
        val targetPort = link.getTargetPort(mutableScheme)

        val source = mutableScheme.nodes[sourcePort.parentNode]!!
        if (source.libEquipmentId == EquipmentLibId.BUS) {
            source.ports.remove(sourcePort)
        }

        val target = mutableScheme.nodes[targetPort.parentNode]!!
        if (target.libEquipmentId == EquipmentLibId.BUS) {
            target.ports.remove(targetPort)
        }

        sourcePort.links -= removingLinkId
        targetPort.links -= removingLinkId

        mutableScheme.nodes -= removingEquipment.id
        mutableScheme.links -= removingLinkId
    }

    /**
     *
     ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
     ░░░░░░│░░░░░░░░░░░░░░░░░░░░░░░░░░░
     ░░░░░░│░░░░░░░░░░░░░░░░░░░░░░░░░░░
     ░░░░░░│░░░░░░░░░░░░░░░░░░░░░░░░░░░
     ░░░░█████░░░░░░░░░░░░░░░░░░░░░░░░░
     ░░░░█████░░reconnectingEquipment░░
     ░░░░█████░░reconnectingPort░░░░░░░
     ░░░░░░│░░░░░░░░░░░░░░░░░░░░░░░░░░░
     ░░░░░░│░░░░removingLink░░░░░░░░░░░
     ░░░░░░│░░░░░░░░░░░░░░░░░░░░░░░░░░░
     ░░░░█████░░firstPort░░░░░░░░░░░░░░
     ░░░░█████░░removingEquipment░░░░░░
     ░░░░█████░░secondPort░░░░░░░░░░░░░
     ░░░░░░│░░░░░░░░░░░░░░░░░░░░░░░░░░░
     ░░░░░░│░░░░reconnectingLink░░░░░░░
     ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
     *
     */
    private fun removeEquipmentWithTwoPortsFromScheme(
        removingEquipment: MutableRawEquipmentNodeDto,
        mutableScheme: MutableRawSchemeDto
    ) {
        val firstPort = removingEquipment.ports[0]
        val secondPort = removingEquipment.ports[1]

        val removingLink = mutableScheme.links[firstPort.links.first()]!!
        val reconnectingLink = mutableScheme.links[secondPort.links.first()]!!

        val reconnectingEquipment = removingLink.getSiblingFromSchemeOf(
            mutableScheme,
            removingEquipment
        )
        val reconnectingPort = reconnectingEquipment.getRelatedPort(removingLink)

        reconnectingPort.links -= removingLink.id
        reconnectingPort.links += reconnectingLink.id

        if (reconnectingLink.sourcePort == secondPort.id) {
            reconnectingLink.sourcePort = reconnectingPort.id
            reconnectingLink.source = reconnectingEquipment.id
        } else {
            reconnectingLink.targetPort = reconnectingPort.id
            reconnectingLink.target = reconnectingEquipment.id
        }

        mutableScheme.nodes -= removingEquipment.id
        mutableScheme.links -= removingLink.id
    }
}
