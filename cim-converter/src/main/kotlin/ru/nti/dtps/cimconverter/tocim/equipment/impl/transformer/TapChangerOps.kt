package ru.nti.dtps.cimconverter.tocim.equipment.impl.transformer

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.getFieldStringValue
import ru.nti.dtps.proto.lib.field.FieldLibId
import java.util.*

fun createTapChangerResourceBuilder(
    equipment: RawEquipmentNodeDto
): RdfResourceBuilder? = if (equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_EXISTENCE) == "enabled") {
    RdfResourceBuilder(UUID.randomUUID().toString(), CimClasses.RatioTapChanger)
        .addDataProperty(
            CimClasses.TapChanger.step,
            equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_DEFAULT_POSITION)
        )
        .addDataProperty(
            CimClasses.TapChanger.highStep,
            equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_MAX_POSITION)
        )
        .addDataProperty(
            CimClasses.TapChanger.lowStep,
            equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_MIN_POSITION)
        )
        .addDataProperty(
            CimClasses.RatioTapChanger.stepVoltageIncrement,
            equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_VOLTAGE_CHANGE)
        )
} else {
    null
}

fun choosePowerTransformerEnd(
    equipment: RawEquipmentNodeDto,
    firstPowerTransformerEnd: RdfResource,
    secondPowerTransformerEnd: RdfResource
): RdfResource = if (equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_INSTALLATION_WINDING) == "onFirstWinding") {
    firstPowerTransformerEnd
} else {
    secondPowerTransformerEnd
}

fun choosePowerTransformerEnd(
    equipment: RawEquipmentNodeDto,
    firstPowerTransformerEnd: RdfResource,
    secondPowerTransformerEnd: RdfResource,
    thirdPowerTransformerEnd: RdfResource
) = if (equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_INSTALLATION_WINDING) == "onFirstWinding") {
    firstPowerTransformerEnd
} else if (equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_INSTALLATION_WINDING) == "onSecondWinding") {
    secondPowerTransformerEnd
} else {
    thirdPowerTransformerEnd
}
