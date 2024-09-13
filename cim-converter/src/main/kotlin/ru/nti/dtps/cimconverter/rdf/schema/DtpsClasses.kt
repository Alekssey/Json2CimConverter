package ru.nti.dtps.cimconverter.rdf.schema

object DtpsClasses {

    val namespace = Namespace.dtps

    object AsynchronousMachine : AbstractCimClass(namespace) {
        val rotorLeakageReactance = CimField(this, "rotorLeakageReactance")
    }

    object ACLineSegment : AbstractCimClass(namespace) {
        val ratedActivePower = CimField(this, "ratedActivePower")
        val useConcentratedParameters = CimField(this, "useConcentratedParameters")
        val DoubleCircuitTransmissionLineContainer = CimField(this, "DoubleCircuitTransmissionLineContainer")
    }

    object DoubleCircuitTransmissionLineSegmentContainer : AbstractCimClass(namespace)

    object EquivalentInjection : AbstractCimClass(namespace) {
        val emfTimeConstant = CimField(this, "emfTimeConstant")
    }

    object ElectricityStorageSystem : AbstractCimClass(namespace) {
        val batteryСapacity = CimField(this, "batteryСapacity")
        val maxCurrent = CimField(this, "maxCurrent")
        val polarizationConstant = CimField(this, "polarizationConstant")
        val internalResistance = CimField(this, "internalResistance")
        val initialCharge = CimField(this, "initialCharge")
        val оperatingMode = CimField(this, "оperatingMode")
        val activeChargeDischargePower = CimField(this, "activeChargeDischargePower")
    }

    object ElectricityStorageSystemMode : AbstractCimClass(namespace) {
        val charge = CimEnum(this, "charge")
        val discharge = CimEnum(this, "discharge")
        val off = CimEnum(this, "off")
    }

    object DcCurrentSource : AbstractCimClass(namespace) {
        val conductivityPhaseA = CimField(this, "conductivityPhaseA")
        val conductivityPhaseB = CimField(this, "conductivityPhaseB")
        val conductivityPhaseC = CimField(this, "conductivityPhaseC")
        val currentPhaseA = CimField(this, "currentPhaseA")
        val currentPhaseB = CimField(this, "currentPhaseB")
        val currentPhaseC = CimField(this, "currentPhaseC")
    }

    object DcCurrentSourceByPhase : AbstractCimClass(namespace) {
        val conductivity = CimField(this, "conductivity")
        val current = CimField(this, "current")
    }

    object DcEmfSource : AbstractCimClass(namespace) {
        val resistancePhaseA = CimField(this, "resistancePhaseA")
        val resistancePhaseB = CimField(this, "resistancePhaseB")
        val resistancePhaseC = CimField(this, "resistancePhaseC")
        val emfPhaseA = CimField(this, "emfPhaseA")
        val emfPhaseB = CimField(this, "emfPhaseB")
        val emfPhaseC = CimField(this, "emfPhaseC")
    }

    object DcEmfSourceByPhase : AbstractCimClass(namespace) {
        val resistance = CimField(this, "resistance")
        val emf = CimField(this, "emf")
    }

    object GovCT1 : AbstractCimClass(Cim302Classes.namespace) {
        val initialPosition = CimField(this, "initialPosition")
        val frequencySetPoint = CimField(this, "frequencySetPoint")
    }

    object PowerTransformerEnd : AbstractCimClass(namespace) {
        val doesSaturationExist = CimField(this, "doesSaturationExist")
    }

    object EquipmentFault : AbstractCimClass(CimClasses.namespace) {
        val enableByUZeroCrossing = CimField(this, "enableByUZeroCrossing")
        val enableByUaZeroCrossing = CimField(this, "enableByUaZeroCrossing")
    }

    object Capacitance : AbstractCimClass(CimClasses.namespace)

    object CurrentTransformer : AbstractCimClass(namespace) {
        val primaryWindingTurnAmount = CimField(this, "primaryWindingTurnAmount")
        val secondaryWindingTurnAmount = CimField(this, "secondaryWindingTurnAmount")
        val coreCrossSection = CimField(this, "coreCrossSection")
        val magneticPathAverageLength = CimField(this, "magneticPathAverageLength")
        val secondaryWindingResistance = CimField(this, "secondaryWindingResistance")
        val secondaryWindingInductance = CimField(this, "secondaryWindingInductance")
        val secondaryWindingLoadResistance = CimField(this, "secondaryWindingLoadResistance")
        val secondaryWindingLoadInductance = CimField(this, "secondaryWindingLoadInductance")
        val magnetizationCurveFirstCoefficient = CimField(this, "magnetizationCurveFirstCoefficient")
        val magnetizationCurveSecondCoefficient = CimField(this, "magnetizationCurveSecondCoefficient")
        val magnetizationCurveThirdCoefficient = CimField(this, "magnetizationCurveThirdCoefficient")
        val modelType = CimField(this, "modelType")
    }

    object CurrentTransformerModelType : AbstractCimClass(CimClasses.namespace) {
        val ideal = CimEnum(this, "ideal")
        val real = CimEnum(this, "real")
    }

    object EnergyConsumer : AbstractCimClass(namespace) {
        val ratedVoltage = CimField(this, "ratedVoltage")
    }

    object PotentialTransformer : AbstractCimClass(namespace) {
        val primaryWindingRatedVoltage = CimField(this, "primaryWindingRatedVoltage")
        val secondaryWindingRatedVoltage = CimField(this, "secondaryWindingRatedVoltage")
    }

    object StaticVarCompensator : AbstractCimClass(namespace) {
        val q = CimField(this, "q")
        val tU = CimField(this, "tU")
    }

    object SynchronousMachine : AbstractCimClass(namespace) {
        val isAvrEnabled = CimField(this, "isAvrEnabled")
        val isArtfEnabled = CimField(this, "isArtfEnabled")
        val isArapEnabled = CimField(this, "isArapEnabled")
        val isAvrEnabledByDefalut = CimField(this, "isAvrEnabledByDefalut")
        val isArtfEnabledByDefalut = CimField(this, "isArtfEnabledByDefalut")
        val isArapEnabledByDefalut = CimField(this, "isArapEnabledByDefalut")
        val initialSpeed = CimField(this, "initialSpeed")
        val initialRotationAngle = CimField(this, "initialRotationAngle")
        val initialEmf = CimField(this, "initialEmf")
    }

    object Resistance : AbstractCimClass(CimClasses.namespace)

    object Inductance : AbstractCimClass(CimClasses.namespace)

    object ThreePhaseConnector : AbstractCimClass(CimClasses.namespace)
}
