package ru.nti.dtps.cimconverter.tocim.votlage.level

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.getFieldStringValueOrNull
import ru.nti.dtps.dto.scheme.isPowerTransformer
import ru.nti.dtps.equipment.meta.info.dataclass.voltagelevel.VoltageLevelLib
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.utils.graph.DtpsRawSchemeSearch
import java.util.*

object VoltageLevelConverter {

    fun convert(
        scheme: RawSchemeDto,
        substations: Map<String, RdfResource>,
        baseVoltages: Map<VoltageLevelLibId, RdfResource>
    ): Pair<Map<String, RdfResource>, Map<String, List<RawEquipmentNodeDto>>> {
        val voltageLevelIdToVoltageLevelResourceMap = mutableMapOf<String, RdfResource>()
        val voltageLevelIdToEquipmentsMap = mutableMapOf<String, List<RawEquipmentNodeDto>>()

        findVoltageLevels(scheme).forEach { (voltageLevel, equipments) ->
            val substationRdfResource = findSubstationForVoltageLevelEquipments(
                scheme,
                substations,
                equipments
            )

            val id = UUID.randomUUID().toString()
            voltageLevelIdToVoltageLevelResourceMap[id] = RdfResourceBuilder(id, CimClasses.VoltageLevel)
                .addObjectProperty(CimClasses.VoltageLevel.BaseVoltage, baseVoltages[voltageLevel.id]!!)
                .apply {
                    substationRdfResource?.let {
                        addObjectProperty(CimClasses.VoltageLevel.Substation, substationRdfResource)
                    }
                }
                .build()

            voltageLevelIdToEquipmentsMap[id] = equipments
        }

        return voltageLevelIdToVoltageLevelResourceMap to voltageLevelIdToEquipmentsMap
    }

    private fun findVoltageLevels(scheme: RawSchemeDto): Map<VoltageLevelLib, List<RawEquipmentNodeDto>> {
        val boundaryPredicate: (equipment: RawEquipmentNodeDto) -> Boolean = { it.isPowerTransformer() }

        val bfs = DtpsRawSchemeSearch.getBreadthFirstSearch(scheme, boundaryPredicate)

        val powerTransformerIdToVoltageLevelLib = mutableMapOf<RawEquipmentNodeDto, VoltageLevelLib>()

        val voltageLevelToEquipmentsMap = mutableMapOf<VoltageLevelLib, MutableList<RawEquipmentNodeDto>>()

        scheme.nodes.values.asSequence()
            .filter { !bfs.hasNodeBeenVisited(it) && !boundaryPredicate(it) }
            .forEach { equipment ->
                val voltageLevel = EquipmentMetaInfoManager.getVoltageLevelById(equipment.voltageLevelId)

                voltageLevelToEquipmentsMap.computeIfAbsent(voltageLevel) { mutableListOf() } += equipment

                bfs.searchFromNode(equipment, doOnSiblingNodeAndPort = { sibling, _ ->
                    if (sibling.isPowerTransformer()) {
                        val powerTransformerVoltageLevel = powerTransformerIdToVoltageLevelLib[sibling]
                        if (
                            powerTransformerVoltageLevel == null ||
                            powerTransformerVoltageLevel.voltageInKilovolts <=
                            voltageLevel.voltageInKilovolts
                        ) {
                            powerTransformerIdToVoltageLevelLib[sibling] = voltageLevel
                        }
                    } else {
                        voltageLevelToEquipmentsMap[voltageLevel]!! += sibling
                    }
                })
            }

        powerTransformerIdToVoltageLevelLib.forEach { (powerTransformer, vl) ->
            voltageLevelToEquipmentsMap[vl]!! += powerTransformer
        }

        return voltageLevelToEquipmentsMap
    }

    private fun findSubstationForVoltageLevelEquipments(
        scheme: RawSchemeDto,
        substations: Map<String, RdfResource>,
        equipments: List<RawEquipmentNodeDto>
    ): RdfResource? {
        val substationIds = scheme.substations.map { it.id }
        val substationIdToElements = mutableMapOf<String, MutableList<RawEquipmentNodeDto>>()

        equipments
            .filter { equipment -> equipment.getFieldStringValueOrNull(FieldLibId.SUBSTATION) in substationIds }
            .forEach { equipment ->
                equipment.getFieldStringValueOrNull(FieldLibId.SUBSTATION)?.let { substationId ->
                    substationIdToElements.computeIfAbsent(substationId) { mutableListOf() } += equipment
                }
            }

        return if (substationIdToElements.isNotEmpty()) {
            val substationId = substationIdToElements.entries.reduce { acc, next ->
                val (_, nextEquipments) = next
                val (_, accEquipments) = acc
                if (nextEquipments.size > accEquipments.size) {
                    next
                } else {
                    acc
                }
            }.key

            substations[substationId]!!
        } else {
            null
        }
    }
}
