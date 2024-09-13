package ru.nti.dtps.cimconverter.tocim.sv

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceId
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.*
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId.*
import ru.nti.dtps.proto.lib.field.FieldLibId
import java.util.*

object SvVoltageConverter {

    fun convert(scheme: RawSchemeDto, linkIdToTnMap: Map<String, RdfResource>): List<RdfResource> {
        val resources = mutableListOf<RdfResource>()

        val topologicalNodesWithVoltage = mutableSetOf<RdfResourceId>()

        scheme.nodes.values.forEach { equipment ->
            getSourceVoltageOrNull(equipment)?.let { voltage ->
                val link = equipment.ports.first().getLinksFromScheme(scheme).first()
                linkIdToTnMap[link.id]?.let { topologicalNode ->
                    if (!topologicalNodesWithVoltage.contains(topologicalNode.id)) {
                        resources += RdfResourceBuilder(
                            UUID.randomUUID().toString(),
                            CimClasses.SvVoltage
                        ).addDataProperty(CimClasses.SvVoltage.v, voltage.magnitudeInKilovolts)
                            .addDataProperty(CimClasses.SvVoltage.angle, voltage.angleInDegree)
                            .addObjectProperty(CimClasses.SvVoltage.TopologicalNode, topologicalNode)
                            .build()

                        topologicalNodesWithVoltage += topologicalNode.id
                    }
                }
            }
        }

        return resources
    }

    private fun getSourceVoltageOrNull(equipment: RawEquipmentNodeDto): Voltage? = when (equipment.libEquipmentId) {
        POWER_SYSTEM_EQUIVALENT -> Voltage(
            magnitudeInKilovolts = equipment.getFieldDoubleValue(FieldLibId.VOLTAGE_LINE_TO_LINE),
            angleInDegree = equipment.getFieldDoubleValue(FieldLibId.ANGLE_OF_PHASE_A)
        )

        else -> null
    }

    class Voltage(
        val magnitudeInKilovolts: Double,
        val angleInDegree: Double
    )
}
