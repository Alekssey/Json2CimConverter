package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous

data class ExcitationSystemDynamics(
    val synchronousMachineDynamicsId: String,
    val channelGainByVoltageDeviationCoefficient: Double?,
    val channelIntegrationTimeConstantForVoltageDeviationCoefficient: Double?,
    val stabilizationChannelGainBasedOnVoltageChangeRate: Double?,
    val stabilizationChannelTimeConstantForVoltageChangeRate: Double?,
    val stabilizationChannelTimeConstantOnDrivingCurrentChangeRate: Double?,
    val drivingSystemTimeConstant: Double?,
    val drivingSystemUpperLimit: Double?,
    val drivingSystemLowerLimit: Double?,
    val drivingSystemGain: Double?
)
