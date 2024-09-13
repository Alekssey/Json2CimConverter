package ru.nti.dpts.schememanagerback.validator

import `in`.rcard.assertj.arrowcore.EitherAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemesForTest
import ru.nti.dpts.schememanagerback.builder.SubstationBuilder
import ru.nti.dpts.schememanagerback.scheme.domain.isDashboardTypeLib
import ru.nti.dpts.schememanagerback.scheme.service.validator.ValidatorService
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.EquipmentCountIsMoreThenLimitError
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.EquipmentParamsValidationError
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.RequiredFieldsValidator
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.SchemeDoesNotContainsRequiredEquipmentError
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

@SpringBootTest(classes = [TestConfigurationToValidation::class])
class Basic {

    @Autowired
    lateinit var validatorService: ValidatorService

    @Test
    fun `validate three winding power transformer fields`() {
        val threeWindingPowerTransformer = EquipmentNodeBuilder().buildThreeWindingPowerTransformer().build()
        assertTrue(RequiredFieldsValidator.validateFields(threeWindingPowerTransformer).isRight())
    }

    @Test
    fun `validate power system equivalent without optional fields`() {
        val requiredFieldsWithoutOptional = EquipmentMetaInfoManager.getEquipmentLibById(EquipmentLibId.POWER_SYSTEM_EQUIVALENT).fields
            .filter { !it.optional }
            .associate { it.id to it.defaultValue }
        val powerSystemEquivalent = EquipmentNodeBuilder()
            .withName("Power system equivalent")
            .withLibEquipmentId(EquipmentLibId.POWER_SYSTEM_EQUIVALENT)
            .withFields(requiredFieldsWithoutOptional)
            .withSubstationId("noId")
            .build()

        assertTrue(RequiredFieldsValidator.validateFields(powerSystemEquivalent).isRight())
    }

    @Test
    fun `throw valid exception if scheme has not power system equivalent`() {
        EitherAssert.assertThat(validatorService.validate(SchemeBuilder().build())).containsLeftInstanceOf(
            SchemeDoesNotContainsRequiredEquipmentError::class.java
        )
    }

    @Test
    fun `throw valid exception if scheme has more than 200 equipments`() {
        val rightBound = 201
        val nodes = (1..rightBound)
            .map { it.toString() }
            .associateWith { EquipmentNodeBuilder().build() }
            .toMutableMap()

        EitherAssert.assertThat(
            validatorService.validate(
                SchemeBuilder().withNodes(nodes).build()
            )
        ).containsLeftInstanceOf(EquipmentCountIsMoreThenLimitError::class.java)
    }

    @Test
    fun `validate scheme with all types equipment`() {
        val scheme = SchemesForTest.buildSchemeWithAllTypesEquipment()

        assertTrue(
            validatorService.validate(
                scheme
            ).isRight()
        )
    }

    @Test
    fun `validate scheme with synch generator and without power system`() {
        val substation = SubstationBuilder().build()
        val busId = UUID.randomUUID().toString()
        val synchGeneratorId = UUID.randomUUID().toString()
        val linkIdFirstPortBus = UUID.randomUUID().toString()

        val bus = EquipmentNodeBuilder().buildBus()
            .withId(busId)
            .withSubstationId(substation.id)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.NO_ID)
                        .withParentNode(busId)
                        .withLinks(listOf(linkIdFirstPortBus))
                        .build()
                )
            )
            .build()

        val synchGenerator = EquipmentNodeBuilder()
            .buildSynchronousGenerator()
            .withId(synchGeneratorId)
            .withSubstationId(substation.id)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(synchGeneratorId)
                        .withLinks(listOf(linkIdFirstPortBus))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    synchGenerator.id to synchGenerator
                )
            )
            .connect(
                linkId = linkIdFirstPortBus,
                sourceEquipment = synchGenerator,
                targetEquipment = bus,
                sourcePort = synchGenerator.ports[0],
                targetPort = bus.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
            )
            .withSubstations(listOf(substation))
            .build()

        assertTrue(
            validatorService.validate(scheme).isRight()
        )
    }

    @Test
    fun `has valid onLeft error if transformer has not valid rpn field values`() {
        val twoPowerTransformer = EquipmentNodeBuilder()
            .buildTwoWindingPowerTransformer()
            .withFields(
                mapOf(
                    FieldLibId.TAP_CHANGER_EXISTENCE to "enabled",
                    FieldLibId.TAP_CHANGER_INSTALLATION_WINDING to "onFirstWinding",
                    FieldLibId.TAP_CHANGER_DEFAULT_POSITION to "12",
                    FieldLibId.TAP_CHANGER_VOLTAGE_CHANGE to "1.5",
                    FieldLibId.TAP_CHANGER_MAX_POSITION to "10",
                    FieldLibId.TAP_CHANGER_MIN_POSITION to "1"
                )
            )
            .build()
        val powerSystemEquivalent = EquipmentNodeBuilder()
            .buildPowerSystemEquivalent()
            .build()
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    twoPowerTransformer.id to twoPowerTransformer,
                    powerSystemEquivalent.id to powerSystemEquivalent
                )
            )
            .build()

        EitherAssert.assertThat(
            validatorService.validate(scheme)
        ).containsLeftInstanceOf(EquipmentParamsValidationError.PowerTransformerRpnValuesError::class.java)
    }

    @Test
    fun `has valid onLeft error if transmission line has not valid field values`() {
        val transmissionLineSegment = EquipmentNodeBuilder()
            .buildTransmissionLineSegment()
            .withFields(
                mapOf(
                    FieldLibId.SUSCEPTANCE_PER_LENGTH_POS_NEG_SEQ to "0.1",
                    FieldLibId.SUSCEPTANCE_PER_LENGTH_ZERO_SEQ to "0.5"
                )
            )
            .build()
        val powerSystemEquivalent = EquipmentNodeBuilder()
            .buildPowerSystemEquivalent()
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    transmissionLineSegment.id to transmissionLineSegment,
                    powerSystemEquivalent.id to powerSystemEquivalent
                )
            )
            .build()

        EitherAssert.assertThat(
            validatorService.validate(scheme)
        ).containsLeftInstanceOf(EquipmentParamsValidationError.LineSegmentPreconditionParamsError::class.java)
    }

    @Test
    fun `has valid onLeft error if transmission line not select as pi-section with relevant field values`() {
        val transmissionLineSegment = EquipmentNodeBuilder()
            .buildTransmissionLineSegment()
            .withFields(
                mapOf(
                    FieldLibId.RESISTANCE_PER_LENGTH_POS_NEG_SEQ to "1",
                    FieldLibId.REACTANCE_PER_LENGTH_POS_NEG_SEQ to "0.0000001",
                    FieldLibId.SUSCEPTANCE_PER_LENGTH_POS_NEG_SEQ to "100",
                    FieldLibId.RESISTANCE_PER_LENGTH_ZERO_SEQ to "1",
                    FieldLibId.REACTANCE_PER_LENGTH_ZERO_SEQ to "1",
                    FieldLibId.SUSCEPTANCE_PER_LENGTH_ZERO_SEQ to "10",
                    FieldLibId.USE_CONCENTRATED_PARAMETERS to "disabled"
                )
            )
            .build()
        val powerSystemEquivalent = EquipmentNodeBuilder()
            .buildPowerSystemEquivalent()
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    transmissionLineSegment.id to transmissionLineSegment,
                    powerSystemEquivalent.id to powerSystemEquivalent
                )
            )
            .build()

        EitherAssert.assertThat(
            validatorService.validate(scheme)
        )
            .containsLeftInstanceOf(EquipmentParamsValidationError.LineSegmentPreconditionConcentratedParamsError::class.java)
    }

    @Test
    fun `throw valid exception if flap state upper limit greater than flap state lower limit in synchronous generator fields`() {
        val synchronousGenerator = EquipmentNodeBuilder()
            .buildSynchronousGenerator()
            .withFields(
                mapOf(
                    FieldLibId.FLAP_STATE_UPPER_LIMIT to "1.0",
                    FieldLibId.FLAP_STATE_LOWER_LIMIT to "10.0"
                )
            )
            .build()

        val powerSystemEquivalent = EquipmentNodeBuilder()
            .buildPowerSystemEquivalent()
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    synchronousGenerator.id to synchronousGenerator,
                    powerSystemEquivalent.id to powerSystemEquivalent
                )
            )
            .build()

        EitherAssert.assertThat(
            validatorService.validate(scheme)
        ).containsLeftInstanceOf(EquipmentParamsValidationError.GeneratorPreconditionParamsError::class.java)
    }

    @Test
    fun `scheme has all types equipment`() {
        val scheme = SchemesForTest.buildSchemeWithAllTypesEquipment()
        val allTypesEquipmentCount =
            EquipmentLibId.values()
                .filter { it != EquipmentLibId.UNRECOGNIZED }
                .filter { !it.isDashboardTypeLib() }
                .size
        val uniqueEquipmentLibIds = scheme.nodes.values.map { it.libEquipmentId }.toSet()

        assertThat(uniqueEquipmentLibIds.size).isEqualTo(allTypesEquipmentCount)
    }
}
