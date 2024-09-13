package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous

data class RotatingMachineDynamics(
    val synchronousMachineId: String,
    val inertiaConstant: Double?,
    val statorLeakageReactance: Double?,
    val viscousFrictionCoefficient: Double?,
    val statorActiveResistance: Double?,
    val id: String
)
