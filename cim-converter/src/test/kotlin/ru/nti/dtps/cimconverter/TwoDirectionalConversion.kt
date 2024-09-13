package ru.nti.dtps.cimconverter

import org.junit.jupiter.api.Test

class TwoDirectionalConversion {

    @Test
    fun `convert ShortCircuits scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/ShortCircuits.json")
    }

    @Test
    fun `convert BusesHorizontalAndVertical scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/BusesHorizontalAndVertical.json")
    }

    @Test
    fun `convert SiblingConnectivities scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/SiblingConnectivities.json")
    }

    @Test
    fun `convert LittleWithConnectivity scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/LittleWithConnectivity.json")
    }

    @Test
    fun `convert LittleWith3WPT scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/LittleWith3WPT.json")
    }

    @Test
    fun `convert LineAndBus scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/LineAndBus.json")
    }

    @Test
    fun `convert ReactivePowerCompensationSystem scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/ReactivePowerCompensationSystem.json")
    }

    @Test
    fun `convert NewElements22_03_24 scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/NewElements22_03_24.json")
    }

    @Test
    fun `convert SynchronousGeneratorAndLoad scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/SynchronousGeneratorAndLoad.json")
    }

    @Test
    fun `convert E_VT scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/E_VT.json")
    }

    @Test
    fun `convert E_CT_BUS scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/E_CT_BUS.json")
    }

    @Test
    fun `convert E_3PhDC scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/E_3PhDC.json")
    }

    @Test
    fun `convert E_DoubleCircuitLine scheme to CIM and back`() {
        val (initial, converted) = convertToCimAndBackAndTest("src/test/resources/scheme/E_DoubleCircuitLine.json")
    }
}
