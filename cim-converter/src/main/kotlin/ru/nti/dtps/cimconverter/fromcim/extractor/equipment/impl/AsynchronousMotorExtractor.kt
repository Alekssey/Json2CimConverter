package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.extractor.EquipmentsExtractionResult
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.RawEquipmentCreator
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.copyWithFields
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.asyncronousmotor.AsynchronousMachineEquivalentCircuit
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.asyncronousmotor.AsynchronousMachineEquivalentCircuitExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.link.PortInfo
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel.VoltageLevel
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractBooleanValueOrFalse
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object AsynchronousMotorExtractor : SealedEquipmentExtractor() {

    private val cimClass = CimClasses.AsynchronousMachine
    private val equipmentLibId = EquipmentLibId.ASYNCHRONOUS_MOTOR

    override fun extract(
        repository: RdfRepository,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        links: List<RawEquipmentLinkDto>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double
    ): EquipmentsExtractionResult {
        val asynchronousMotorIdToAsynchronousMachineEqCircuitMap =
            AsynchronousMachineEquivalentCircuitExtractor.extract(
                repository
            )

        return EquipmentsExtractionResult(
            equipments = create(
                repository.selectAllVarsFromTriples(
                    cimClass,
                    CimClasses.IdentifiedObject.name,
                    CimClasses.Equipment.EquipmentContainer,
                    CimClasses.ConductingEquipment.BaseVoltage,
                    CimClasses.RotatingMachine.ratedU,
                    CimClasses.AsynchronousMachine.nominalFrequency,
                    CimClasses.AsynchronousMachine.ratedMechanicalPower,
                    CimClasses.AsynchronousMachine.reversible,
                    DtpsClasses.AsynchronousMachine.rotorLeakageReactance
                ),
                substations,
                lines,
                baseVoltages,
                voltageLevels,
                equipmentIdToPortsMap,
                objectIdToDiagramObjectMap,
                asynchronousMotorIdToAsynchronousMachineEqCircuitMap
            )
        )
    }

    private fun create(
        queryResult: TupleQueryResult,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        asynchronousMotorIdToAsynchronousMachineEqCircuitMap: Map<String, AsynchronousMachineEquivalentCircuit>
    ): List<RawEquipmentNodeDto> = queryResult.mapIndexed { index, bindingSet ->
        val asynchronousMotorId = bindingSet.extractIdentifiedObjectId()

        val asynchronousMachineEquivalentCircuit = asynchronousMotorIdToAsynchronousMachineEqCircuitMap[
            asynchronousMotorId
        ]

        RawEquipmentCreator.equipmentWithBaseData(
            bindingSet,
            substations,
            lines,
            baseVoltages,
            voltageLevels,
            index + 1,
            equipmentLibId,
            equipmentIdToPortsMap,
            objectIdToDiagramObjectMap
        ).copyWithFields(
            FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to bindingSet.extractDoubleValueOrNull(
                CimClasses.RotatingMachine.ratedU
            ),
            FieldLibId.RATED_ACTIVE_POWER to bindingSet.extractDoubleValueOrNull(
                CimClasses.AsynchronousMachine.ratedMechanicalPower
            ),
            FieldLibId.FREQUENCY to (
                bindingSet.extractDoubleValueOrNull(
                    CimClasses.AsynchronousMachine.nominalFrequency
                ) ?: 50.0
                ),
            FieldLibId.ROTOR_LEAKAGE_INDUCTANCE_REFERRED_TO_STATOR to bindingSet.extractDoubleValueOrNull(
                DtpsClasses.AsynchronousMachine.rotorLeakageReactance
            ),
            FieldLibId.REVERSE_ROTATION_ENABLED to if (
                bindingSet.extractBooleanValueOrFalse(CimClasses.AsynchronousMachine.reversible)
            ) {
                "enabled"
            } else {
                "disabled"
            }
        ).let { resource ->
            if (asynchronousMachineEquivalentCircuit != null) {
                resource.copyWithFields(
                    FieldLibId.INERTIA_CONSTANT to asynchronousMachineEquivalentCircuit.inertia,
                    FieldLibId.DAMPING_COEFFICIENT to asynchronousMachineEquivalentCircuit.damping,
                    FieldLibId.STATOR_ACTIVE_RESISTANCE to asynchronousMachineEquivalentCircuit.statorResistance,
                    FieldLibId.MUTUAL_INDUCTANCE_OF_ROTOR_STATOR to asynchronousMachineEquivalentCircuit.xm
                )
            } else {
                resource
            }
        }
    }
}
