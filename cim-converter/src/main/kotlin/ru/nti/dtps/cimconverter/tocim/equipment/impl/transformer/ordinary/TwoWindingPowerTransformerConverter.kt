package ru.nti.dtps.cimconverter.tocim.equipment.impl.transformer.ordinary

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
import ru.nti.dtps.equipment.meta.info.manager.winding.TwoWindings
import ru.nti.dtps.equipment.meta.info.manager.winding.WindingTypeManager
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object TwoWindingPowerTransformerConverter : AbstractEquipmentConversion() {
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

        val twoWindingsType: TwoWindings = WindingTypeManager.parseWindingTypes(
            equipment.getFieldStringValue(FieldLibId.FIRST_WINDING_TYPE),
            equipment.getFieldStringValue(FieldLibId.SECOND_WINDING_TYPE)
        )

        val ratioTapChangerBuiler = createTapChangerResourceBuilder(equipment)

        val firstPowerTransformerEnd = createFirstWindingPowerTransformerEndResource(
            equipment = equipment,
            powerTransformer = resource,
            terminals = equipment.ports
                .filter { it.libId == PortLibId.FIRST }
                .map(RawEquipmentNodeDto.PortDto::id)
                .mapNotNull(portIdToTerminalMap::get),
            baseVoltages = baseVoltages,
            windingType = twoWindingsType.firstWindingType,
            ratioTapChangerId = ratioTapChangerBuiler?.id
        )

        val secondPowerTransformerEnd = createSecondWindingPowerTransformerEndResource(
            equipment = equipment,
            powerTransformer = resource,
            terminals = equipment.ports
                .filter { it.libId == PortLibId.SECOND }
                .map(RawEquipmentNodeDto.PortDto::id)
                .mapNotNull(portIdToTerminalMap::get),
            baseVoltages = baseVoltages,
            windingType = twoWindingsType.secondWindingType,
            ratioTapChangerId = ratioTapChangerBuiler?.id
        )

        ratioTapChangerBuiler?.addObjectProperty(
            CimClasses.RatioTapChanger.TransformerEnd,
            choosePowerTransformerEnd(
                equipment,
                firstPowerTransformerEnd,
                secondPowerTransformerEnd
            )
        )

        return EquipmentRelatedResources(
            resource,
            portIdToTerminalMap,
            listOf(
                firstPowerTransformerEnd,
                secondPowerTransformerEnd
            ) + (ratioTapChangerBuiler?.let(RdfResourceBuilder::build)?.let(::listOf) ?: emptyList())
        )
    }
}
