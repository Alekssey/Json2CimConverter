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
import ru.nti.dtps.equipment.meta.info.manager.winding.ThreeWindings
import ru.nti.dtps.equipment.meta.info.manager.winding.WindingTypeManager
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object ThreeWindingPowerTransformerConverter : AbstractEquipmentConversion() {
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

        val threeWindingsType: ThreeWindings = WindingTypeManager.parseWindingTypes(
            equipment.getFieldStringValue(FieldLibId.FIRST_WINDING_TYPE),
            equipment.getFieldStringValue(FieldLibId.SECOND_WINDING_TYPE),
            equipment.getFieldStringValue(FieldLibId.THIRD_WINDING_TYPE)
        )

        val ratioTapChangerBuilder = createTapChangerResourceBuilder(equipment)

        val firstPowerTransformerEnd = createFirstWindingPowerTransformerEndResource(
            equipment = equipment,
            powerTransformer = resource,
            terminals = equipment.ports
                .filter { it.libId == PortLibId.FIRST }
                .map(RawEquipmentNodeDto.PortDto::id)
                .mapNotNull(portIdToTerminalMap::get),
            baseVoltages = baseVoltages,
            windingType = threeWindingsType.firstWindingType,
            ratioTapChangerId = ratioTapChangerBuilder?.id
        )

        val secondPowerTransformerEnd = createSecondWindingPowerTransformerEndResource(
            equipment = equipment,
            powerTransformer = resource,
            terminals = equipment.ports
                .filter { it.libId == PortLibId.SECOND }
                .map(RawEquipmentNodeDto.PortDto::id)
                .mapNotNull(portIdToTerminalMap::get),
            baseVoltages = baseVoltages,
            windingType = threeWindingsType.secondWindingType,
            ratioTapChangerId = ratioTapChangerBuilder?.id
        )

        val thirdPowerTransformerEnd = createThirdWindingPowerTransformerEndResource(
            equipment = equipment,
            powerTransformer = resource,
            terminals = equipment.ports
                .filter { it.libId == PortLibId.THIRD }
                .map(RawEquipmentNodeDto.PortDto::id)
                .mapNotNull(portIdToTerminalMap::get),
            baseVoltages = baseVoltages,
            windingType = threeWindingsType.thirdWindingType,
            ratioTapChangerId = ratioTapChangerBuilder?.id
        )

        ratioTapChangerBuilder?.addObjectProperty(
            CimClasses.RatioTapChanger.TransformerEnd,
            choosePowerTransformerEnd(
                equipment,
                firstPowerTransformerEnd,
                secondPowerTransformerEnd,
                thirdPowerTransformerEnd
            )
        )

        return EquipmentRelatedResources(
            resource,
            portIdToTerminalMap,
            listOf(
                firstPowerTransformerEnd,
                secondPowerTransformerEnd,
                thirdPowerTransformerEnd
            ) + (ratioTapChangerBuilder?.let(RdfResourceBuilder::build)?.let(::listOf) ?: emptyList())
        )
    }
}
