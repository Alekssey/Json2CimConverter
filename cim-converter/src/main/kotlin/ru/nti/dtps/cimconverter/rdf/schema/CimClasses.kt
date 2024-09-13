package ru.nti.dtps.cimconverter.rdf.schema

import kotlin.reflect.full.memberProperties

object CimClasses {

    val namespace = Namespace.cim

    object RotatingMachine : AbstractCimClass(namespace) {
        val ratedU = CimField(this, "ratedU")
    }

    object ACDCTerminal : AbstractCimClass(namespace) {
        val sequenceNumber = CimField(this, "sequenceNumber")
    }

    object ACLineSegment : AbstractCimClass(namespace) {
        val r = CimField(this, "r")
        val r0 = CimField(this, "r0")
        val x = CimField(this, "x")
        val x0 = CimField(this, "x0")
        val bch = CimField(this, "bch")
        val b0ch = CimField(this, "b0ch")
    }

    object AsynchronousMachine : AbstractCimClass(namespace) {
        val p = CimField(this, "p")
        val rr1 = CimField(this, "rr1")
        val rr2 = CimField(this, "rr2")
        val xlr1 = CimField(this, "xlr1")
        val xs = CimField(this, "xs")
        val rm = CimField(this, "rm")
        val xm = CimField(this, "xm")
        val nominalFrequency = CimField(this, "nominalFrequency")
        val reversible = CimField(this, "reversible")
        val ratedMechanicalPower = CimField(this, "ratedMechanicalPower")
    }

    object AuxiliaryEquipment : AbstractCimClass(namespace) {
        val Terminal = CimField(this, "Terminal")
    }

    object BaseVoltage : AbstractCimClass(namespace) {
        val nominalVoltage = CimField(this, "nominalVoltage")
    }

    object BaseFrequency : AbstractCimClass(namespace) {
        val frequency = CimField(this, "frequency")
    }

    object CableInfo : AbstractCimClass(namespace)

    object SteamTurbine : AbstractCimClass(namespace)

    object DrumBoiler : AbstractCimClass(namespace)

    object Breaker : AbstractCimClass(namespace)

    object Disconnector : AbstractCimClass(namespace)

    object GroundDisconnector : AbstractCimClass(namespace)

    object BusbarSection : AbstractCimClass(namespace)

    object Jumper : AbstractCimClass(namespace)

    object OperationalLimitSet : AbstractCimClass(namespace)

    object RegulatingControl : AbstractCimClass(namespace)

    object HydroTurbine : AbstractCimClass(namespace)

    object Analog : AbstractCimClass(namespace)

    object AnalogValue : AbstractCimClass(namespace)

    object DiscreteValue : AbstractCimClass(namespace)

    object ThermalGeneratingUnit : AbstractCimClass(namespace)

    object LinearShuntCompensator : AbstractCimClass(namespace)

    object ConnectivityNode : AbstractCimClass(namespace) {
        val TopologicalNode = CimField(this, "TopologicalNode")
    }

    object TopologicalNode : AbstractCimClass(namespace)

    object ConductingEquipment : AbstractCimClass(namespace) {
        val BaseVoltage = CimField(this, "BaseVoltage")
    }

    object CurrentTransformer : AbstractCimClass(namespace) {
        val accuracyClass = CimField(this, "accuracyClass")
    }

    object Conductor : AbstractCimClass(namespace) {
        val length = CimField(this, "length")
    }

    object Diagram : AbstractCimClass(namespace) {
        val orientation = CimField(this, "orientation")
        val x1InitialView = CimField(this, "x1InitialView")
        val x2InitialView = CimField(this, "x2InitialView")
        val y1InitialView = CimField(this, "y1InitialView")
        val y2InitialView = CimField(this, "y2InitialView")
    }

    object DiagramObject : AbstractCimClass(namespace) {
        val Diagram = CimField(this, "Diagram")
        val IdentifiedObject = CimField(this, "IdentifiedObject")
        val rotation = CimField(this, "rotation")
    }

    object DiagramObjectPoint : AbstractCimClass(namespace) {
        val DiagramObject = CimField(this, "DiagramObject")
        val sequenceNumber = CimField(this, "sequenceNumber")
        val xPosition = CimField(this, "xPosition")
        val yPosition = CimField(this, "yPosition")
    }

    object Fault : AbstractCimClass(namespace) {
        val FaultyEquipment = CimField(this, "FaultyEquipment")
        val impedance = CimField(this, "impedance")
        val kind = CimField(this, "kind")
        val phases = CimField(this, "phases")
    }

    object EquipmentFault : AbstractCimClass(namespace) {
        val Terminal = CimField(this, "Terminal")
    }

    object EnergyConsumer : AbstractCimClass(namespace) {
        val p = CimField(this, "p")
        val q = CimField(this, "q")
        val pfixed = CimField(this, "pfixed")
        val qfixed = CimField(this, "qfixed")
        val grounded = CimField(this, "grounded")
        val LoadResponse = CimField(this, "LoadResponse")
    }

    object EquivalentInjection : AbstractCimClass(namespace) {
        val maxQ = CimField(this, "maxQ")
        val minQ = CimField(this, "minQ")
        val p = CimField(this, "p")
        val q = CimField(this, "q")
        val r = CimField(this, "r")
        val r2 = CimField(this, "r2")
        val r0 = CimField(this, "r0")
        val x = CimField(this, "x")
        val x2 = CimField(this, "x2")
        val x0 = CimField(this, "x0")
    }

    object FaultImpedance : AbstractCimClass(namespace) {
        val rGround = CimField(this, "rGround")
        val rLineToLine = CimField(this, "rLineToLine")
        val xGround = CimField(this, "xGround")
        val xLineToLine = CimField(this, "xLineToLine")
    }

    object IdentifiedObject : AbstractCimClass(namespace) {
        val mRid = CimField(this, "mRID")
        val name = CimField(this, "name")
        val DiagramObjects = CimField(this, "DiagramObjects")
    }

    object Line : AbstractCimClass(namespace)

    object Length : AbstractCimClass(namespace), CimUnit {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
        override fun getDefaultMultiplier() = 1.0
    }

    object Ground : AbstractCimClass(namespace)

    object PotentialTransformer : AbstractCimClass(namespace)

    object PowerTransformer : AbstractCimClass(namespace) {
        val isPartOfGeneratorUnit = CimField(this, "isPartOfGeneratorUnit")
    }

    object PowerTransformerEnd : AbstractCimClass(namespace) {
        val PowerTransformer = CimField(this, "PowerTransformer")
        val ratedU = CimField(this, "ratedU")
        val ratedS = CimField(this, "ratedS")
        val connectionKind = CimField(this, "connectionKind")
        val phaseAngleClock = CimField(this, "phaseAngleClock")
        val x = CimField(this, "x")
        val r = CimField(this, "r")
        val g = CimField(this, "g")
        val b = CimField(this, "b")
    }

    object TapChanger : AbstractCimClass(namespace) {
        val step = CimField(this, "step")
        val highStep = CimField(this, "highStep")
        val lowStep = CimField(this, "lowStep")
    }

    object RatioTapChanger : AbstractCimClass(namespace) {
        val TransformerEnd = CimField(this, "TransformerEnd")
        val stepVoltageIncrement = CimField(this, "stepVoltageIncrement")
    }

    object SeriesCompensator : AbstractCimClass(namespace) {
        val r = CimField(this, "r")
    }

    object Switch : AbstractCimClass(namespace) {
        val open = CimField(this, "open")
        val ratedCurrent = CimField(this, "ratedCurrent")
    }

    object Equipment : AbstractCimClass(namespace) {
        val NormallyInService = CimField(this, "normallyInService")
        val EquipmentContainer = CimField(this, "EquipmentContainer")
    }

    object Terminal : AbstractCimClass(namespace) {
        val TopologicalNode = CimField(this, "TopologicalNode")
        val ConnectivityNode = CimField(this, "ConnectivityNode")
        val ConductingEquipment = CimField(this, "ConductingEquipment")
        val phases = CimField(this, "phases")
    }

    object TransformerEnd : AbstractCimClass(namespace) {
        val Terminal = CimField(this, "Terminal")
        val RatioTapChanger = CimField(this, "RatioTapChanger")
        val endNumber = CimField(this, "endNumber")
        val grounded = CimField(this, "grounded")
        val magBaseU = CimField(this, "magBaseU")
        val bmagSat = CimField(this, "bmagSat")
        val magSatFlux = CimField(this, "magSatFlux")
    }

    object VoltageLevel : AbstractCimClass(namespace) {
        val BaseVoltage = CimField(this, "BaseVoltage")
        val Substation = CimField(this, "Substation")
    }

    object SynchronousMachine : AbstractCimClass(namespace) {
        val ratedU = CimField(this, "ratedU")
        val ratedS = CimField(this, "ratedS")
        val voltageRegulationRange = CimField(this, "voltageRegulationRange")
    }

    object ShuntCompensator : AbstractCimClass(namespace) {
        val nomU = CimField(this, "nomU")
        val aVRDelay = CimField(this, "aVRDelay")
    }

    object StaticVarCompensator : AbstractCimClass(namespace) {
        val q = CimField(this, "q")
        val voltageSetPoint = CimField(this, "voltageSetPoint")
        val slope = CimField(this, "slope")
    }

    object AngleDegrees : AbstractCimClass(namespace) {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
    }

    object Voltage : AbstractCimClass(namespace), CimUnit {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
        override fun getDefaultMultiplier() = 1e3
    }

    object ActivePower : AbstractCimClass(namespace), CimUnit {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
        override fun getDefaultMultiplier() = 1e6
    }

    object ReactivePower : AbstractCimClass(namespace), CimUnit {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
        override fun getDefaultMultiplier() = 1e6
    }

    object ApparentPower : AbstractCimClass(namespace), CimUnit {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
        override fun getDefaultMultiplier() = 1e6
    }

    object PerCent : AbstractCimClass(namespace) {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
    }

    object Conductance : AbstractCimClass(namespace), CimUnit {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
        override fun getDefaultMultiplier() = 1.0
    }

    object Resistance : AbstractCimClass(namespace), CimUnit {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
        override fun getDefaultMultiplier() = 1.0
    }

    object Suseptance : AbstractCimClass(namespace), CimUnit {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
        override fun getDefaultMultiplier() = 1.0
    }

    object Reactance : AbstractCimClass(namespace), CimUnit {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
        override fun getDefaultMultiplier() = 1.0
    }

    object Seconds : AbstractCimClass(namespace), CimUnit {
        val value = CimField(this, "value")
        val unit = CimField(this, "unit")
        val multiplier = CimField(this, "multiplier")
        override fun getDefaultMultiplier() = 1.0
    }

    object LoadResponseCharacteristic : AbstractCimClass(namespace) {
        val pVoltageExponent = CimField(this, "pVoltageExponent")
        val qVoltageExponent = CimField(this, "qVoltageExponent")
        val qConstantPower = CimField(this, "qConstantPower")
    }

    object Plant : AbstractCimClass(namespace)

    object Substation : AbstractCimClass(namespace)

    object SvVoltage : AbstractCimClass(namespace) {
        val angle = CimField(this, "angle")
        val v = CimField(this, "v")
        val TopologicalNode = CimField(this, "TopologicalNode")
    }

    object PhaseCode : AbstractCimClass(namespace) {
        val ABC = CimEnum(this, "ABC")
        val AB = CimEnum(this, "AB")
        val BC = CimEnum(this, "BC")
        val AC = CimEnum(this, "AC")
        val A = CimEnum(this, "A")
        val B = CimEnum(this, "B")
        val C = CimEnum(this, "C")

        val singlePhaseCodes = setOf(A, B, C)

        fun parseOrNull(phaseCode: String): CimEnum? {
            var result: CimEnum? = null

            phaseCode.split(".").lastOrNull()?.let { phaseCodeString ->
                PhaseCode::class.memberProperties.forEach { member ->
                    val value = member.getter.call(this)
                    if (value is CimEnum && value.name == phaseCodeString) {
                        result = value
                    }
                }
            }

            return result
        }
    }

    object PhaseConnectedFaultKind : AbstractCimClass(namespace) {
        val lineToGround = CimEnum(this, "lineToGround")
        val lineToLine = CimEnum(this, "lineToLine")
        val lineToLineToGround = CimEnum(this, "lineToLineToGround")
    }

    object WindingConnection : AbstractCimClass(namespace) {
        val D = CimEnum(this, "D")
        val Y = CimEnum(this, "Y")
        val Yn = CimEnum(this, "Yn")
    }

    object OrientationKind : AbstractCimClass(namespace) {
        val negative = CimEnum(this, "negative")
    }
}
