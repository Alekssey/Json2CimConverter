package ru.nti.dtps.cimconverter.tocim.equipment.auxiliary

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.getFieldDoubleValue
import ru.nti.dtps.proto.lib.field.FieldLibId

object VoltageTransformerConverter {
    fun convert(
        scheme: RawSchemeDto,
        voltageTransformer: RawEquipmentNodeDto,
        equipmentIdToResourceMap: Map<String, RdfResource>,
        portIdToTerminalResourceMap: Map<String, RdfResource>
    ): RdfResource {
        val (_, nearestTerminalResource) = findNearestEquipmentAndPort(
            voltageTransformer,
            scheme,
            equipmentIdToResourceMap,
            portIdToTerminalResourceMap
        )

        return RdfResourceBuilder(voltageTransformer.id, CimClasses.PotentialTransformer)
            .addDataProperty(CimClasses.IdentifiedObject.name, voltageTransformer.name())
            .addDataProperty(
                DtpsClasses.PotentialTransformer.primaryWindingRatedVoltage,
                voltageTransformer.getFieldDoubleValue(FieldLibId.FIRST_WINDING_RATED_VOLTAGE)
            )
            .addDataProperty(
                DtpsClasses.PotentialTransformer.secondaryWindingRatedVoltage,
                voltageTransformer.getFieldDoubleValue(FieldLibId.SECOND_WINDING_RATED_VOLTAGE)
            )
            .addObjectProperty(CimClasses.AuxiliaryEquipment.Terminal, nearestTerminalResource)
            .build()
    }
}
