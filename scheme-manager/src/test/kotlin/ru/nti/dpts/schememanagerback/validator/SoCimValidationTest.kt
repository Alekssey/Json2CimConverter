package ru.nti.dpts.schememanagerback.validator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.SchemeValidator

@SpringBootTest(classes = [TestConfigurationToValidation::class])
class SoCimValidationTest {

    @Autowired
    lateinit var validators: List<SchemeValidator>

    private val objectMapper = jacksonObjectMapper()

    // TODO: need fix
//    @Test
//    fun `CIM by System Operator is valid`() {
//        val schemeString = CimConverter.fromCim(
//            File("src/test/resources/cim/Морозовская ЭС_ГОСТ.xml").inputStream()
//        )
//
//        val scheme = objectMapper.readValue(schemeString, SchemeDomain::class.java)
//
//        assertDoesNotThrow {
//            validators
//                .sortedBy(SchemeValidator::getLevelNumber)
//                .filter { it !is EquipmentsNumberValidator }
//                .forEach { it.validate(scheme) }
//        }
//    }
}
