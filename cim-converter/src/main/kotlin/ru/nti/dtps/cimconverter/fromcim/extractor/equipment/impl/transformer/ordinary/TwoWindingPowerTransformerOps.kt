package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.ordinary

import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.copyWithFields
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.PowerTransformerEnd
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.proto.lib.field.FieldLibId

object TwoWindingPowerTransformerOps {

    fun copyWithSpecificFields(
        powerTransformer: RawEquipmentNodeDto,
        firstWindingPowerTransformerEnd: PowerTransformerEnd?
    ): RawEquipmentNodeDto {
        val ratedVoltageInVolts = firstWindingPowerTransformerEnd
            ?.ratedU
            ?.let { it * CimClasses.Voltage.getDefaultMultiplier() }

        val ratedApparentPowerInVoltsAmpere = firstWindingPowerTransformerEnd
            ?.ratedS
            ?.let { it * CimClasses.ApparentPower.getDefaultMultiplier() }

        return if (ratedVoltageInVolts == null || ratedApparentPowerInVoltsAmpere == null) {
            powerTransformer
        } else {
            if (firstWindingPowerTransformerEnd.x == null) {
                powerTransformer
            } else {
                powerTransformer.copyWithFields(
                    FieldLibId.SHORT_CIRCUIT_VOLTAGE to
                        100 * ratedApparentPowerInVoltsAmpere * firstWindingPowerTransformerEnd.x /
                        (ratedVoltageInVolts * ratedVoltageInVolts)
                )
            }.let {
                if (firstWindingPowerTransformerEnd.r == null) {
                    it
                } else {
                    it.copyWithFields(
                        FieldLibId.SHORT_CIRCUIT_ACTIVE_POWER to firstWindingPowerTransformerEnd.r
                            * ratedApparentPowerInVoltsAmpere * ratedApparentPowerInVoltsAmpere /
                            (ratedVoltageInVolts * ratedVoltageInVolts * 1e3)
                    )
                }
            }.let {
                if (firstWindingPowerTransformerEnd.g == null) {
                    it
                } else {
                    it.copyWithFields(
                        FieldLibId.IDLING_ACTIVE_POWER to firstWindingPowerTransformerEnd.g
                            * ratedVoltageInVolts * ratedVoltageInVolts * 1e-3
                    )
                }
            }.let {
                if (firstWindingPowerTransformerEnd.b == null) {
                    it
                } else {
                    it.copyWithFields(
                        FieldLibId.IDLING_CURRENT to firstWindingPowerTransformerEnd.b
                            * 100 * ratedVoltageInVolts * ratedVoltageInVolts /
                            ratedApparentPowerInVoltsAmpere
                    )
                }
            }
        }
    }
}
