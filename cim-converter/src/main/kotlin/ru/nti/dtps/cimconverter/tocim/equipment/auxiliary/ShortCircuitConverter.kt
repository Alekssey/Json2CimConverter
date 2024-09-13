package ru.nti.dtps.cimconverter.tocim.equipment.auxiliary

import ru.nti.dtps.cimconverter.rdf.UnitsConverter
import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.CimEnum
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.dto.scheme.*
import ru.nti.dtps.equipment.meta.info.manager.shortcircuit.ShortCircuitType
import ru.nti.dtps.equipment.meta.info.manager.shortcircuit.ShortCircuitTypeManager
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object ShortCircuitConverter {

    fun convert(
        scheme: RawSchemeDto,
        shortCircuit: RawEquipmentNodeDto,
        equipmentIdToResourceMap: Map<String, RdfResource>,
        portIdToTerminalResourceMap: Map<String, RdfResource>
    ): RdfResource {
        val (nearestEquipmentResource, nearestTerminalResource) = findNearestEquipmentAndPort(
            shortCircuit,
            scheme,
            equipmentIdToResourceMap,
            portIdToTerminalResourceMap
        )

        return RdfResourceBuilder(shortCircuit.id, CimClasses.EquipmentFault)
            .addDataProperty(CimClasses.IdentifiedObject.name, shortCircuit.name())
            .addDataProperty(
                CimClasses.FaultImpedance.rGround,
                UnitsConverter.withDefaultCimMultiplier(
                    shortCircuit.getFieldValueWithMultiplier(FieldLibId.RESISTANCE),
                    CimClasses.Resistance
                )
            )
            .addObjectProperty(CimClasses.Fault.FaultyEquipment, nearestEquipmentResource)
            .addObjectProperty(CimClasses.EquipmentFault.Terminal, nearestTerminalResource)
            .apply {
                if (shortCircuit.libEquipmentId == EquipmentLibId.SHORT_CIRCUIT_1PH) {
                    addDataProperty(
                        DtpsClasses.EquipmentFault.enableByUZeroCrossing,
                        shortCircuit.getFieldStringValueOrNull(FieldLibId.ENABLE_BY_U_ZERO_CROSSING) == "enabled"
                    )
                } else if (shortCircuit.libEquipmentId == EquipmentLibId.SHORT_CIRCUIT) {
                    val (kind, phases) = convertShortCircuitTypeToCimTypes(
                        ShortCircuitTypeManager.parseShortCircuitType(
                            shortCircuit.getControlStringValue(ControlLibId.SHORT_CIRCUIT_TYPE)
                        )
                    )

                    addDataProperty(
                        DtpsClasses.EquipmentFault.enableByUaZeroCrossing,
                        shortCircuit.getFieldStringValueOrNull(FieldLibId.ENABLE_BY_UA_ZERO_CROSSING) == "enabled"
                    )
                    addEnumProperty(CimClasses.Fault.kind, kind)
                    addEnumProperty(CimClasses.Fault.phases, phases)
                }
            }
            .build()
    }
}

private fun convertShortCircuitTypeToCimTypes(
    shortCircuitType: ShortCircuitType
): Pair<CimEnum, CimEnum> {
    return when (shortCircuitType) {
        ShortCircuitType.ABC ->
            CimClasses.PhaseConnectedFaultKind.lineToLine to CimClasses.PhaseCode.ABC

        ShortCircuitType.ABCG ->
            CimClasses.PhaseConnectedFaultKind.lineToLineToGround to CimClasses.PhaseCode.ABC

        ShortCircuitType.BC ->
            CimClasses.PhaseConnectedFaultKind.lineToLine to CimClasses.PhaseCode.BC

        ShortCircuitType.BCG ->
            CimClasses.PhaseConnectedFaultKind.lineToLineToGround to CimClasses.PhaseCode.BC

        ShortCircuitType.AG ->
            CimClasses.PhaseConnectedFaultKind.lineToGround to CimClasses.PhaseCode.A
    }
}
