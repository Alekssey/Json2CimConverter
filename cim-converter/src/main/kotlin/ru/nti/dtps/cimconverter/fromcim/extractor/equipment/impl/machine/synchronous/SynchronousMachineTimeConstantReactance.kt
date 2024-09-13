package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous

data class SynchronousMachineTimeConstantReactance(
    val synchronousMachineId: String,
    val drivingWindingLeakageReactance: Double?
)
