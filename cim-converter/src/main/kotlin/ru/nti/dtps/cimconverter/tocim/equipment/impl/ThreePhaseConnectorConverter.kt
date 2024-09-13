package ru.nti.dtps.cimconverter.tocim.equipment.impl

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.CimEnum
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.cimconverter.tocim.equipment.AbstractEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.EquipmentRelatedResources
import ru.nti.dtps.cimconverter.tocim.equipment.baseEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.convertPorts
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object ThreePhaseConnectorConverter : AbstractEquipmentConversion() {
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
        val resource = RdfResourceBuilder(equipment.id, DtpsClasses.ThreePhaseConnector)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource) { portLibId ->
                defineTerminalPhaseCode(equipment, portLibId)
            }
        )
    }

    private fun defineTerminalPhaseCode(
        equipment: RawEquipmentNodeDto,
        portLibId: PortLibId
    ): CimEnum = when (equipment.libEquipmentId) {
        EquipmentLibId.THREE_PHASE_CONNECTOR -> when (portLibId) {
            PortLibId.UNRECOGNIZED, PortLibId.NO_ID, PortLibId.FIRST -> CimClasses.PhaseCode.ABC
            PortLibId.SECOND -> CimClasses.PhaseCode.A
            PortLibId.THIRD -> CimClasses.PhaseCode.B
            PortLibId.FOURTH -> CimClasses.PhaseCode.C
        }

        else -> CimClasses.PhaseCode.ABC
    }
}
