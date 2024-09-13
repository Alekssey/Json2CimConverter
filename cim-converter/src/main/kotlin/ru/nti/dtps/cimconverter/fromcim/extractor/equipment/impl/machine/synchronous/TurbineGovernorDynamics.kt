package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous

data class TurbineGovernorDynamics(
    val synchronousMachineDynamicsId: String,
    val turbineTimeConstant: Double?,
    val turbinePowerPu: Double?,
    val turbineInitialState: Double?,
    val speedRelayGain: Double?,
    val flapStateLowerLimit: Double?,
    val flapStateUpperLimit: Double?,
    val frequencySetPoint: Double?,
    val powerSetPoint: Double?,
    val integrationOfPowerRegulationConstant: Double?
)
