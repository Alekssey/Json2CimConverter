package ru.nti.dtps.cimconverter.tocim.equipment

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.CimEnum
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.isTransmissionLine
import ru.nti.dtps.proto.lib.port.PortLibId

fun RdfResourceBuilder.baseEquipmentConversion(
    equipment: RawEquipmentNodeDto,
    baseVoltage: RdfResource,
    voltageLevel: RdfResource,
    line: RdfResource? = null
): RdfResourceBuilder {
    addDataProperty(CimClasses.IdentifiedObject.name, equipment.name())
    addObjectProperty(CimClasses.ConductingEquipment.BaseVoltage, baseVoltage)
    if (equipment.isTransmissionLine()) {
        line?.let { addObjectProperty(CimClasses.Equipment.EquipmentContainer, line) }
    } else {
        addObjectProperty(CimClasses.Equipment.EquipmentContainer, voltageLevel)
    }
    return this
}

fun convertPorts(
    equipment: RawEquipmentNodeDto,
    linkIdToTnMap: Map<String, RdfResource>,
    linkIdToCnMap: Map<String, RdfResource>,
    equipmentRdfResource: RdfResource,
    definePhaseCode: (portLibId: PortLibId) -> CimEnum = { CimClasses.PhaseCode.ABC }
): Map<String, RdfResource> {
    return equipment.ports.mapIndexed { index, port ->
        port.id to convertPort(
            equipment,
            port,
            index + 1,
            linkIdToTnMap,
            linkIdToCnMap,
            equipmentRdfResource,
            definePhaseCode(port.libId)
        )
    }.toMap()
}

fun convertPort(
    equipment: RawEquipmentNodeDto,
    port: RawEquipmentNodeDto.PortDto,
    number: Int,
    linkIdToTnMap: Map<String, RdfResource>,
    linkIdToCnMap: Map<String, RdfResource>,
    equipmentRdfResource: RdfResource,
    phaseCode: CimEnum = CimClasses.PhaseCode.ABC
): RdfResource = RdfResourceBuilder(port.id, CimClasses.Terminal)
    .addDataProperty(CimClasses.IdentifiedObject.name, "${equipment.name()} $number")
    .addDataProperty(CimClasses.ACDCTerminal.sequenceNumber, number)
    .addObjectProperty(CimClasses.Terminal.ConductingEquipment, equipmentRdfResource)
    .addObjectProperty(CimClasses.Terminal.TopologicalNode, linkIdToTnMap[port.links.first()]!!)
    .addObjectProperty(CimClasses.Terminal.ConnectivityNode, linkIdToCnMap[port.links.first()]!!)
    .addEnumProperty(CimClasses.Terminal.phases, phaseCode)
    .build()
