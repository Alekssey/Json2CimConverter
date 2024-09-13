package ru.nti.dtps.cimconverter.tocim.equipment.impl.transformer

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.tocim.equipment.baseEquipmentConversion
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.getFieldDoubleValue
import ru.nti.dtps.dto.scheme.getFieldStringValue
import ru.nti.dtps.proto.lib.field.FieldLibId

fun createPowerTransformerResource(
    equipment: RawEquipmentNodeDto,
    baseVoltage: RdfResource,
    voltageLevel: RdfResource
): RdfResource = RdfResourceBuilder(equipment.id, CimClasses.PowerTransformer)
    .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
    .addDataProperty(CimClasses.PowerTransformer.isPartOfGeneratorUnit, false)
    .addDataProperty(
        CimClasses.TransformerEnd.magBaseU,
        equipment.getFieldStringValue(FieldLibId.MAGNETIZATION_VOLTAGE)
    )
    .addDataProperty(
        CimClasses.TransformerEnd.bmagSat,
        equipment.getFieldStringValue(FieldLibId.AIR_CORE_RESISTANCE)
    )
    .addDataProperty(
        CimClasses.TransformerEnd.magSatFlux,
        equipment.getFieldDoubleValue(FieldLibId.SATURATION_COEFFICIENT) * 100
    )
    .build()
