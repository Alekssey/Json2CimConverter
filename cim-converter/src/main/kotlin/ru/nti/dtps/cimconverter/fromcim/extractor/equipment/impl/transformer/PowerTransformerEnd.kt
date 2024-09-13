package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer

import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.equipment.meta.info.manager.winding.WindingType

class PowerTransformerEnd(
    val id: String,
    val saturationExists: Boolean,
    val ratedU: Double?,
    val ratedS: Double?,
    val baseVoltage: BaseVoltage?,
    val tapChanger: TapChanger?,
    val endNumber: Int?,
    val windingType: WindingType,
    val x: Double?,
    val r: Double?,
    val g: Double?,
    val b: Double?
) {
    fun isFirstWinding() = endNumber == 1
    fun isSecondWinding() = endNumber == 2
    fun isThirdWinding() = endNumber == 3
}
