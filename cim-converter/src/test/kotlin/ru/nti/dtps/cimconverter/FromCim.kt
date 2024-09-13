package ru.nti.dtps.cimconverter

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class FromCim {

    @Test
    @Disabled("Scheme graph is not connected due to absence of ConnectivityNodesg")
    fun `Rusgidro Sincal`() {
        val scheme = convertToSchemeAndTest("src/test/resources/cim/sincal/РусГидро СахалинЭнерго.xml")
    }

    @Test
    fun `Morozovskaya by System Operator`() {
        val scheme = convertToSchemeAndTest("src/test/resources/cim/so/Морозовская ЭС_ГОСТ.xml")
    }
}
