package ru.nti.dtps.cimconverter.tocim.equipment.impl

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.cimconverter.tocim.equipment.AbstractEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.EquipmentRelatedResources
import ru.nti.dtps.cimconverter.tocim.equipment.baseEquipmentConversion
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.getFieldStringValueOrNull
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import java.util.*

object TransmissionLineSegmentDoubleCircuitConverter : AbstractEquipmentConversion() {
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
        val resource = RdfResourceBuilder(equipment.id, DtpsClasses.DoubleCircuitTransmissionLineSegmentContainer)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .build()

        val firstSegment = TransmissionLineSegmentConverter.convert(
            equipment.toFirstSegmentEquipment(),
            scheme,
            baseVoltage,
            baseVoltages,
            linkIdToTnMap,
            linkIdToCnMap,
            voltageLevel,
            lines
        )

        val secondSegment = TransmissionLineSegmentConverter.convert(
            equipment.toSecondSegmentEquipment(),
            scheme,
            baseVoltage,
            baseVoltages,
            linkIdToTnMap,
            linkIdToCnMap,
            voltageLevel,
            lines
        )

        return EquipmentRelatedResources(
            resource,
            firstSegment.portIdToTerminalResourceMap + secondSegment.portIdToTerminalResourceMap,
            listOf(
                firstSegment.equipmentResource.copyWithObjectProperty(
                    DtpsClasses.ACLineSegment.DoubleCircuitTransmissionLineContainer,
                    resource
                ),
                secondSegment.equipmentResource.copyWithObjectProperty(
                    DtpsClasses.ACLineSegment.DoubleCircuitTransmissionLineContainer,
                    resource
                )
            )
        )
    }

    private fun RawEquipmentNodeDto.toFirstSegmentEquipment(): RawEquipmentNodeDto = copy(
        id = UUID.randomUUID().toString(),
        libEquipmentId = EquipmentLibId.TRANSMISSION_LINE_SEGMENT,
        ports = this.ports.filter { port ->
            port.libId == PortLibId.FIRST || port.libId == PortLibId.SECOND
        },
        fields = this.fields + (
            FieldLibId.TRANSMISSION_LINE to getFieldStringValueOrNull(FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE)
            )
    )

    private fun RawEquipmentNodeDto.toSecondSegmentEquipment(): RawEquipmentNodeDto = copy(
        id = UUID.randomUUID().toString(),
        libEquipmentId = EquipmentLibId.TRANSMISSION_LINE_SEGMENT,
        ports = this.ports
            .mapNotNull { port ->
                val newPortLibId = when (port.libId) {
                    PortLibId.THIRD -> PortLibId.FIRST
                    PortLibId.FOURTH -> PortLibId.SECOND
                    else -> null
                }

                newPortLibId?.let { port.copy(libId = newPortLibId) }
            },
        fields = this.fields + (
            FieldLibId.TRANSMISSION_LINE to getFieldStringValueOrNull(FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE)
            )
    )
}
