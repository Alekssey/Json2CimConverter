package ru.nti.dtps.cimconverter.tocim.equipment.impl.asynchronousmotor

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.Cim302Classes
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.getFieldStringValue
import ru.nti.dtps.proto.lib.field.FieldLibId
import java.util.*

fun createAsynchronousMachineEquivalentCircuitResource(
    asynchronousMotorResource: RdfResource,
    equipment: RawEquipmentNodeDto
): RdfResource = RdfResourceBuilder(UUID.randomUUID().toString(), Cim302Classes.AsynchronousMachineEquivalentCircuit)
    .addObjectProperty(
        Cim302Classes.AsynchronousMachineDynamics.AsynchronousMachine,
        asynchronousMotorResource
    )
    .addDataProperty(
        Cim302Classes.RotatingMachineDynamics.damping,
        equipment.getFieldStringValue(FieldLibId.DAMPING_COEFFICIENT)
    )
    .addDataProperty(
        Cim302Classes.RotatingMachineDynamics.statorResistance,
        equipment.getFieldStringValue(FieldLibId.STATOR_ACTIVE_RESISTANCE)
    )
    .addDataProperty(
        Cim302Classes.RotatingMachineDynamics.inertia,
        equipment.getFieldStringValue(FieldLibId.INERTIA_CONSTANT)
    )
    .addDataProperty(
        Cim302Classes.AsynchronousMachineEquivalentCircuit.xm,
        equipment.getFieldStringValue(FieldLibId.MUTUAL_INDUCTANCE_OF_ROTOR_STATOR)
    )
    .build()
