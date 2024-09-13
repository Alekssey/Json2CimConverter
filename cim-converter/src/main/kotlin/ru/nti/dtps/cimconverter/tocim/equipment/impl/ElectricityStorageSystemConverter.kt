package ru.nti.dtps.cimconverter.tocim.equipment.impl

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.cimconverter.tocim.equipment.AbstractEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.EquipmentRelatedResources
import ru.nti.dtps.cimconverter.tocim.equipment.baseEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.convertPorts
import ru.nti.dtps.dto.scheme.*
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object ElectricityStorageSystemConverter : AbstractEquipmentConversion() {
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
        val resource = RdfResourceBuilder(equipment.id, DtpsClasses.ElectricityStorageSystem)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .addDataProperty(
                DtpsClasses.ElectricityStorageSystem.batteryСapacity,
                equipment.getFieldDoubleValue(FieldLibId.CAPACITY)
            )
            .addDataProperty(
                DtpsClasses.ElectricityStorageSystem.maxCurrent,
                equipment.getFieldDoubleValue(FieldLibId.MAX_CURRENT)
            )
            .addDataProperty(
                DtpsClasses.ElectricityStorageSystem.polarizationConstant,
                equipment.getFieldDoubleValue(FieldLibId.POLARIZATION_CONSTANT)
            )
            .addDataProperty(
                DtpsClasses.ElectricityStorageSystem.internalResistance,
                equipment.getFieldDoubleValue(FieldLibId.INTERNAL_RESISTANCE)
            )
            .addDataProperty(
                DtpsClasses.ElectricityStorageSystem.initialCharge,
                equipment.getFieldDoubleValue(FieldLibId.INITIAL_CHARGE_PERCENTAGE)
            )
            .addEnumProperty(
                DtpsClasses.ElectricityStorageSystem.оperatingMode,
                when (equipment.getFieldStringValue(FieldLibId.ELECTRICITY_STORAGE_SYSTEM_MODE)) {
                    "charge" -> DtpsClasses.ElectricityStorageSystemMode.charge
                    "discharge" -> DtpsClasses.ElectricityStorageSystemMode.discharge
                    else -> DtpsClasses.ElectricityStorageSystemMode.off
                }
            )
            .addDataProperty(
                DtpsClasses.ElectricityStorageSystem.activeChargeDischargePower,
                equipment.getFieldDoubleValue(FieldLibId.ACTIVE_POWER_OF_CHARGE_DISCHARGE)
            )
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource)
        )
    }
}
