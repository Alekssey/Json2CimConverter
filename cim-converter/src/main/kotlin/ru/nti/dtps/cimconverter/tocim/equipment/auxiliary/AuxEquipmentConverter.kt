package ru.nti.dtps.cimconverter.tocim.equipment.auxiliary

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

object AuxEquipmentConverter {

    val auxEquipmentLibIds = setOf(
        EquipmentLibId.SHORT_CIRCUIT,
        EquipmentLibId.SHORT_CIRCUIT_1PH,
        EquipmentLibId.VOLTAGE_TRANSFORMER
    )

    fun convert(
        scheme: RawSchemeDto,
        equipmentIdToResourceMap: Map<String, RdfResource>,
        portIdToTerminalResourceMap: Map<String, RdfResource>
    ): Map<String, RdfResource> = scheme.nodes.values
        .filter { auxEquipmentLibIds.contains(it.libEquipmentId) }
        .associate { auxEquipment ->
            auxEquipment.id to when (auxEquipment.libEquipmentId) {
                EquipmentLibId.SHORT_CIRCUIT, EquipmentLibId.SHORT_CIRCUIT_1PH -> ShortCircuitConverter.convert(
                    scheme,
                    auxEquipment,
                    equipmentIdToResourceMap,
                    portIdToTerminalResourceMap
                )

                EquipmentLibId.VOLTAGE_TRANSFORMER -> VoltageTransformerConverter.convert(
                    scheme,
                    auxEquipment,
                    equipmentIdToResourceMap,
                    portIdToTerminalResourceMap
                )

                else -> throw IllegalArgumentException(
                    "Conversion of equipment ${auxEquipment.libEquipmentId} isn't expected here"
                )
            }
        }
}
