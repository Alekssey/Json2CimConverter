package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.*
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.equipment.meta.info.manager.winding.WindingType

object PowerTransformerEndExtractor {

    fun extract(
        repository: RdfRepository,
        baseVoltages: Map<String, BaseVoltage>,
        powerTransformerEndIdToTapChangerMap: Map<String, TapChanger>
    ): Map<String, List<PowerTransformerEnd>> = create(
        repository.selectAllVarsFromTriples(
            CimClasses.PowerTransformerEnd,
            CimClasses.ConductingEquipment.BaseVoltage,
            CimClasses.PowerTransformerEnd.PowerTransformer,
            CimClasses.TransformerEnd.endNumber,
            CimClasses.TransformerEnd.grounded,
            CimClasses.PowerTransformerEnd.phaseAngleClock,
            CimClasses.PowerTransformerEnd.connectionKind,
            CimClasses.PowerTransformerEnd.ratedU,
            CimClasses.PowerTransformerEnd.ratedS,
            CimClasses.PowerTransformerEnd.x,
            CimClasses.PowerTransformerEnd.r,
            CimClasses.PowerTransformerEnd.g,
            CimClasses.PowerTransformerEnd.b,
            DtpsClasses.PowerTransformerEnd.doesSaturationExist
        ),
        baseVoltages,
        powerTransformerEndIdToTapChangerMap
    )

    fun create(
        queryResult: TupleQueryResult,
        baseVoltages: Map<String, BaseVoltage>,
        powerTransformerEndIdToTapChangerMap: Map<String, TapChanger>
    ): Map<String, List<PowerTransformerEnd>> {
        val powerTransformerIdToPowerTransformerEnds = mutableMapOf<String, MutableList<PowerTransformerEnd>>()

        queryResult.forEach { bindingSet ->
            val id = bindingSet.extractIdentifiedObjectId()
            val baseVoltageId = bindingSet.extractObjectReferenceOrNull(
                CimClasses.ConductingEquipment.BaseVoltage
            )
            val powerTransformerId = bindingSet.extractObjectReferenceOrNull(
                CimClasses.PowerTransformerEnd.PowerTransformer
            )
            powerTransformerId?.let {
                powerTransformerIdToPowerTransformerEnds.computeIfAbsent(powerTransformerId) { mutableListOf() }.add(
                    PowerTransformerEnd(
                        id = id,
                        saturationExists = bindingSet.extractBooleanValueOrFalse(DtpsClasses.PowerTransformerEnd.doesSaturationExist),
                        ratedU = bindingSet.extractDoubleValueOrNull(CimClasses.PowerTransformerEnd.ratedU),
                        ratedS = bindingSet.extractDoubleValueOrNull(CimClasses.PowerTransformerEnd.ratedS),
                        baseVoltage = baseVoltages[baseVoltageId],
                        tapChanger = powerTransformerEndIdToTapChangerMap[id],
                        endNumber = bindingSet.extractIntValueOrNull(CimClasses.TransformerEnd.endNumber),
                        windingType = convertConnectionKind(
                            bindingSet.extractStringValueOrNull(
                                CimClasses.PowerTransformerEnd.connectionKind
                            ) ?: "Y",
                            bindingSet.extractBooleanValueOrFalse(CimClasses.TransformerEnd.grounded),
                            bindingSet.extractIntValueOrNull(CimClasses.PowerTransformerEnd.phaseAngleClock) ?: 0
                        ),
                        x = bindingSet.extractDoubleValueOrNull(CimClasses.PowerTransformerEnd.x),
                        r = bindingSet.extractDoubleValueOrNull(CimClasses.PowerTransformerEnd.r),
                        g = bindingSet.extractDoubleValueOrNull(CimClasses.PowerTransformerEnd.g),
                        b = bindingSet.extractDoubleValueOrNull(CimClasses.PowerTransformerEnd.b)
                    )
                )
            }
        }

        return powerTransformerIdToPowerTransformerEnds
    }

    private fun convertConnectionKind(
        connectionKind: String,
        grounded: Boolean,
        phaseAngleClock: Int
    ): WindingType {
        val connectionKindValue = connectionKind.split("WindingConnection.").getOrNull(1)

        return if (connectionKindValue == null) {
            WindingType.Y
        } else {
            when (connectionKindValue) {
                "D" -> when (phaseAngleClock) {
                    1 -> WindingType.D1
                    else -> WindingType.D11
                }

                else -> if (grounded) WindingType.Yg else WindingType.Y
            }
        }
    }
}
