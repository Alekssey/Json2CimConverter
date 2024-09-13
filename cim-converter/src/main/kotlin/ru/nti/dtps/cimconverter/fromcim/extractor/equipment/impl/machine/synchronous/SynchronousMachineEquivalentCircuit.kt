package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous

data class SynchronousMachineEquivalentCircuit(
    val synchronousMachineId: String,
    val statorRotorMutualInductanceQAxis: Double?,
    val statorRotorMutualInductanceDAxis: Double?,
    val firstDamperWindingLeakageReactanceQAxis: Double?,
    val drivingWindingResistance: Double?,
    val firstDamperWindingResistanceQAxis: Double?,
    val damperWindingResistanceDAxis: Double?,
    val secondDamperWindingResistanceQAxis: Double?,
    val damperWindingLeakageReactanceDAxis: Double?,
    val secondDamperWindingLeakageReactanceQAxis: Double?,
    val damperWindingDrivingMutualIndactanceDAxis: Double?
)
