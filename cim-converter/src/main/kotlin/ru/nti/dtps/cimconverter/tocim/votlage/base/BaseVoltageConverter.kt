package ru.nti.dtps.cimconverter.tocim.votlage.base

import ru.nti.dtps.cimconverter.rdf.UnitsConverter
import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.equipment.meta.info.dataclass.common.Language
import ru.nti.dtps.equipment.meta.info.dataclass.voltagelevel.VoltageLevelLib
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import java.util.*

object BaseVoltageConverter {

    fun convert(scheme: RawSchemeDto): Map<VoltageLevelLibId, RdfResource> {
        val baseVoltages = findBaseVoltages(scheme)

        return baseVoltages.associate { vl ->
            vl.id to RdfResourceBuilder(UUID.randomUUID().toString(), CimClasses.BaseVoltage)
                .addDataProperty(CimClasses.IdentifiedObject.name, vl.name[Language.RU]!!)
                .addDataProperty(
                    CimClasses.BaseVoltage.nominalVoltage,
                    UnitsConverter.withDefaultCimMultiplier(
                        vl.voltageInKilovolts * 1000,
                        CimClasses.Voltage
                    )
                ).build()
        }
    }

    private fun findBaseVoltages(scheme: RawSchemeDto): List<VoltageLevelLib> {
        val baseVoltages = mutableSetOf<VoltageLevelLibId>()
        scheme.nodes.values.forEach {
            if (it.voltageLevelId != null) {
                baseVoltages.add(it.voltageLevelId!!)
            }
        }

        return baseVoltages.map { EquipmentMetaInfoManager.getVoltageLevelById(it) }
    }
}
