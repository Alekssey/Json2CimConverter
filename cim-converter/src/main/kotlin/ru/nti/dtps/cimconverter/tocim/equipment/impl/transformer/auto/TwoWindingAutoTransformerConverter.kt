package ru.nti.dtps.cimconverter.tocim.equipment.impl.transformer.auto

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.tocim.equipment.AbstractEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.EquipmentRelatedResources
import ru.nti.dtps.cimconverter.tocim.equipment.convertPort
import ru.nti.dtps.cimconverter.tocim.equipment.impl.transformer.*
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.getFieldStringValue
import ru.nti.dtps.equipment.meta.info.manager.winding.WindingTypeManager
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object TwoWindingAutoTransformerConverter : AbstractEquipmentConversion() {
    override fun convert(
        equipment: RawEquipmentNodeDto,
        scheme: RawSchemeDto,
        baseVoltage: RdfResource,
        baseVoltages: Map<VoltageLevelLibId, RdfResource>,
        linkIdToTnMap: Map<String, RdfResource>,
        linkIdToCnMap: Map<String, RdfResource>,
        voltageLevel: RdfResource,
        lines: Map<String, RdfResource>
    ): EquipmentRelatedResources {
        val resource = createPowerTransformerResource(equipment, baseVoltage, voltageLevel)

        val portIdToTerminalMap: Map<String, RdfResource> = equipment.ports.mapIndexed { index, port ->
            port.id to convertPort(equipment, port, index + 1, linkIdToTnMap, linkIdToCnMap, resource)
        }.toMap()

        val windingType = WindingTypeManager.parseWindingType(
            equipment.getFieldStringValue(FieldLibId.FIRST_WINDING_TYPE)
        )

        val ratioTapChangerBuilder = createTapChangerResourceBuilder(equipment)

        val firstPowerTransformerEnd = createFirstWindingPowerTransformerEndResource(
            equipment = equipment,
            powerTransformer = resource,
            terminals = equipment.ports.map(RawEquipmentNodeDto.PortDto::id).mapNotNull(portIdToTerminalMap::get),
            baseVoltages = baseVoltages,
            windingType = windingType,
            ratioTapChangerId = ratioTapChangerBuilder?.id
        )

        ratioTapChangerBuilder?.addObjectProperty(
            CimClasses.RatioTapChanger.TransformerEnd,
            firstPowerTransformerEnd
        )

        return EquipmentRelatedResources(
            resource,
            portIdToTerminalMap,
            listOf(firstPowerTransformerEnd) + (
                ratioTapChangerBuilder?.let(RdfResourceBuilder::build)?.let(::listOf) ?: emptyList()
                )
        )
    }
}
