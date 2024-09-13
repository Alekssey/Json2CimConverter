package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.*
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object TapChangerExtractor {

    private val cimClass = CimClasses.RatioTapChanger

    fun extract(repository: RdfRepository): Map<String, TapChanger> = create(
        repository.selectAllVarsFromTriples(
            cimClass,
            CimClasses.RatioTapChanger.TransformerEnd,
            CimClasses.TapChanger.step,
            CimClasses.TapChanger.highStep,
            CimClasses.TapChanger.lowStep,
            CimClasses.RatioTapChanger.stepVoltageIncrement
        )
    )

    fun create(
        queryResult: TupleQueryResult
    ): Map<String, TapChanger> {
        val powerTransformerEndIdToTapChangerMap = mutableMapOf<String, TapChanger>()

        queryResult.forEach { bindingSet ->
            val id = bindingSet.extractIdentifiedObjectId()
            val powerTransformerEndId = bindingSet.extractObjectReferenceOrNull(
                CimClasses.RatioTapChanger.TransformerEnd
            )
            powerTransformerEndId?.let {
                powerTransformerEndIdToTapChangerMap[powerTransformerEndId] = TapChanger(
                    id,
                    defaultPosition = bindingSet.extractIntValueOrNull(CimClasses.TapChanger.step)
                        ?: EquipmentMetaInfoManager.getFieldByEquipmentLibIdAndFieldLibId(
                            EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER,
                            FieldLibId.TAP_CHANGER_DEFAULT_POSITION
                        ).defaultValue!!.toInt(),
                    maxPosition = bindingSet.extractIntValueOrNull(CimClasses.TapChanger.highStep)
                        ?: EquipmentMetaInfoManager.getFieldByEquipmentLibIdAndFieldLibId(
                            EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER,
                            FieldLibId.TAP_CHANGER_MAX_POSITION
                        ).defaultValue!!.toInt(),
                    minPosition = bindingSet.extractIntValueOrNull(CimClasses.TapChanger.lowStep)
                        ?: EquipmentMetaInfoManager.getFieldByEquipmentLibIdAndFieldLibId(
                            EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER,
                            FieldLibId.TAP_CHANGER_MIN_POSITION
                        ).defaultValue!!.toInt(),
                    voltageChange = bindingSet.extractDoubleValueOrNull(CimClasses.RatioTapChanger.stepVoltageIncrement)
                        ?: EquipmentMetaInfoManager.getFieldByEquipmentLibIdAndFieldLibId(
                            EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER,
                            FieldLibId.TAP_CHANGER_VOLTAGE_CHANGE
                        ).defaultValue!!.toDouble()
                )
            }
        }

        return powerTransformerEndIdToTapChangerMap
    }
}
