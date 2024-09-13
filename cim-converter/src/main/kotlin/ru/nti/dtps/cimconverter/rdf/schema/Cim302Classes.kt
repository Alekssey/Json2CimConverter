package ru.nti.dtps.cimconverter.rdf.schema

object Cim302Classes {

    val namespace = Namespace.cim302

    object AsynchronousMachineDynamics : AbstractCimClass(namespace) {
        val AsynchronousMachine = CimField(this, "AsynchronousMachine")
    }

    object ExcitationSystemDynamics : AbstractCimClass(namespace) {
        val SynchronousMachineDynamics = CimField(this, "SynchronousMachineDynamics")
    }

    object ExcREXS : AbstractCimClass(namespace) {
        val kvp = CimField(this, "kvp")
        val tb1 = CimField(this, "tb1")
        val kd = CimField(this, "kd")
        val ta = CimField(this, "ta")
        val te = CimField(this, "te")
        val tf = CimField(this, "tf")
        val kf = CimField(this, "kf")
        val vrmax = CimField(this, "vrmax")
        val vrmin = CimField(this, "vrmin")
    }

    object GovCT1 : AbstractCimClass(namespace) {
        val tb = CimField(this, "tb")
        val ldref = CimField(this, "ldref")
        val ka = CimField(this, "ka")
        val rdown = CimField(this, "rdown")
        val rup = CimField(this, "rup")
        val mwbase = CimField(this, "mwbase")
        val kigov = CimField(this, "kigov")
    }

    object RotatingMachineDynamics : AbstractCimClass(namespace) {
        val inertia = CimField(this, "inertia")
        val damping = CimField(this, "damping")
        val statorResistance = CimField(this, "statorResistance")
        val statorLeakageReactance = CimField(this, "statorLeakageReactance")
    }

    object SynchronousMachineDynamics : AbstractCimClass(namespace) {
        val SynchronousMachine = CimField(this, "SynchronousMachine")
    }

    object SynchronousMachineEquivalentCircuit : AbstractCimClass(namespace) {
        val xaq = CimField(this, "xaq")
        val xad = CimField(this, "xad")
        val xf1d = CimField(this, "xf1d")
        val rfd = CimField(this, "rfd")
        val r1q = CimField(this, "r1q")
        val r1d = CimField(this, "r1d")
        val r2q = CimField(this, "r2q")
        val x1d = CimField(this, "x1d")
        val x2q = CimField(this, "x2q")
        val xfd = CimField(this, "xfd")
    }

    object SynchronousMachineTimeConstantReactance : AbstractCimClass(namespace) {
        val xDirectSync = CimField(this, "xDirectSync")
    }

    object TurbineGovernorDynamics : AbstractCimClass(namespace) {
        val SynchronousMachineDynamics = CimField(this, "SynchronousMachineDynamics")
    }

    object AsynchronousMachineEquivalentCircuit : AbstractCimClass(namespace) {
        val xm = CimField(this, "xm")
    }
}
