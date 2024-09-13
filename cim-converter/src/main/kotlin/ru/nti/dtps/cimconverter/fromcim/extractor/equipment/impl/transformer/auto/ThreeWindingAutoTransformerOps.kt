package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.auto

import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.copyWithFields
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.PowerTransformerEnd
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.proto.lib.field.FieldLibId

object ThreeWindingAutoTransformerOps {

    fun copyWithSpecificFields(
        powerTransformer: RawEquipmentNodeDto,
        firstWindingPowerTransformerEnd: PowerTransformerEnd?,
        secondWindingPowerTransformerEnd: PowerTransformerEnd?,
        thirdWindingPowerTransformerEnd: PowerTransformerEnd?
    ): RawEquipmentNodeDto {
        val ratedApparentPowerInVoltsAmpere = firstWindingPowerTransformerEnd
            ?.ratedS
            ?.let { it * CimClasses.ApparentPower.getDefaultMultiplier() }

        val firstWindingRatedVoltageInVolts = firstWindingPowerTransformerEnd
            ?.ratedU
            ?.let { it * CimClasses.Voltage.getDefaultMultiplier() }

        val secondWindingRatedVoltageInVolts = secondWindingPowerTransformerEnd
            ?.ratedU
            ?.let { it * CimClasses.Voltage.getDefaultMultiplier() }

        val thirdWindingRatedVoltageInVolts = thirdWindingPowerTransformerEnd
            ?.ratedU
            ?.let { it * CimClasses.Voltage.getDefaultMultiplier() }

        return if (
            firstWindingRatedVoltageInVolts == null ||
            ratedApparentPowerInVoltsAmpere == null ||
            secondWindingRatedVoltageInVolts == null ||
            thirdWindingRatedVoltageInVolts == null
        ) {
            powerTransformer
        } else {
            val squaredFirstWindingRatedVoltageInVolts =
                firstWindingRatedVoltageInVolts * firstWindingRatedVoltageInVolts
            if (firstWindingPowerTransformerEnd.x == null ||
                secondWindingPowerTransformerEnd.x == null ||
                thirdWindingPowerTransformerEnd.x == null
            ) {
                powerTransformer
            } else {
                powerTransformer.copyWithFields(
                    FieldLibId.FIRST_SECOND_WINDING_SHORT_CIRCUIT_VOLTAGE to
                        (100 * 1e-6 * ratedApparentPowerInVoltsAmpere / squaredFirstWindingRatedVoltageInVolts) *
                        (firstWindingPowerTransformerEnd.x + secondWindingPowerTransformerEnd.x)
                ).copyWithFields(
                    FieldLibId.FIRST_THIRD_WINDING_SHORT_CIRCUIT_VOLTAGE to
                        (100 * 1e-6 * ratedApparentPowerInVoltsAmpere / squaredFirstWindingRatedVoltageInVolts) *
                        (firstWindingPowerTransformerEnd.x + thirdWindingPowerTransformerEnd.x)
                ).copyWithFields(
                    FieldLibId.SECOND_THIRD_WINDING_SHORT_CIRCUIT_VOLTAGE to
                        (100 * 1e-6 * ratedApparentPowerInVoltsAmpere / squaredFirstWindingRatedVoltageInVolts) *
                        (secondWindingPowerTransformerEnd.x + thirdWindingPowerTransformerEnd.x)
                )
            }.let {
                if (firstWindingPowerTransformerEnd.r == null) {
                    it
                } else {
                    it.copyWithFields(
                        FieldLibId.SHORT_CIRCUIT_ACTIVE_POWER to firstWindingPowerTransformerEnd.r
                            * ratedApparentPowerInVoltsAmpere * ratedApparentPowerInVoltsAmpere /
                            (squaredFirstWindingRatedVoltageInVolts * 1e3)
                    )
                }
            }.let {
                if (firstWindingPowerTransformerEnd.g == null) {
                    it
                } else {
                    it.copyWithFields(
                        FieldLibId.IDLING_ACTIVE_POWER to firstWindingPowerTransformerEnd.g
                            * squaredFirstWindingRatedVoltageInVolts * 1e-3
                    )
                }
            }.let {
                if (firstWindingPowerTransformerEnd.b == null) {
                    it
                } else {
                    it.copyWithFields(
                        FieldLibId.IDLING_CURRENT to firstWindingPowerTransformerEnd.b
                            * 100 * squaredFirstWindingRatedVoltageInVolts /
                            ratedApparentPowerInVoltsAmpere
                    )
                }
            }
        }
    }
}
