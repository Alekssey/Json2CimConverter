package ru.nti.dpts.schememanagerback.validator

import `in`.rcard.assertj.arrowcore.EitherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.XyDto
import ru.nti.dpts.schememanagerback.scheme.service.augmentation.AugmentationService
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.EquipmentParamsValidationError
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.RequiredFieldsValidator
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

class RequiredFieldsValidator {

    private val augmentationService = AugmentationService()
    private val requiredFieldsValidator = RequiredFieldsValidator()

    @Test
    fun `Button dashboard equipment successfully validated after augmentation`() {
        val button = EquipmentNodeDomain(
            UUID.randomUUID().toString(),
            voltageLevelId = null,
            libEquipmentId = EquipmentLibId.BUTTON,
            coords = XyDto(0.0, 0.0),
            dimensions = EquipmentNodeDomain.SizeDto(1.0, 2.0),
            fields = mutableMapOf(
                FieldLibId.BUTTON_VALUE_WHEN_PRESSED to "1.0",
                FieldLibId.NAME to "NodeName"
            )
        )

        val scheme = SchemeDomain()
        scheme.dashboardNodes[button.id] = button
        augmentationService.augment(scheme)

        val validationResult = requiredFieldsValidator.validate(scheme)
        assertAll(
            { Assertions.assertTrue(validationResult.isRight()) },
            { Assertions.assertTrue(scheme.dashboardNodes[button.id]!!.controls.containsKey(ControlLibId.BUTTON_VALUE_WHEN_PRESSED)) }
        )
    }

    @Test
    fun `Button dashboard equipment has error while augmentation`() {
        val button = EquipmentNodeDomain(
            UUID.randomUUID().toString(),
            voltageLevelId = null,
            libEquipmentId = EquipmentLibId.BUTTON,
            coords = XyDto(0.0, 0.0),
            dimensions = EquipmentNodeDomain.SizeDto(1.0, 2.0),
            fields = mapOf(FieldLibId.NAME to "NodeName")
        )

        val scheme = SchemeDomain()
        scheme.dashboardNodes[button.id] = button

        val validationResult = requiredFieldsValidator.validate(scheme)
        assertAll(
            { Assertions.assertTrue(validationResult.isLeft()) }
        )
    }

    @Test
    fun `has valid onLeft error if transmission line has LINE_PARAMETERS_TYPE=template but template not selected`() {
        val powerSystemEquivalentId = UUID.randomUUID().toString()
        val powerSystemEquivalent = EquipmentNodeBuilder()
            .buildPowerSystemEquivalent()
            .withId(powerSystemEquivalentId)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
            .withSubstationId("noId")
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(powerSystemEquivalentId)
                        .withLinks(listOf(UUID.randomUUID().toString()))
                        .build()
                )
            )
            .build()

        val lineSegmentId = UUID.randomUUID().toString()
        val transmissionLineSegment = EquipmentNodeBuilder()
            .buildTransmissionLineSegment()
            .withId(lineSegmentId)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
            .withTransmissionLineId("noId")
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(lineSegmentId)
                        .withLinks(listOf(powerSystemEquivalent.ports[0].id))
                        .build(),
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.SECOND)
                        .withParentNode(lineSegmentId)
                        .withLinks(listOf(UUID.randomUUID().toString()))
                        .build()
                )
            )
            .withFields(
                mapOf(
                    FieldLibId.VOLTAGE_LEVEL to "KILOVOLTS_110",
                    FieldLibId.LINE_PARAMETERS_TYPE to "template",
                    FieldLibId.LINE_TEMPLATE to null
                )
            )
            .build()

        val loadId = UUID.randomUUID().toString()
        val load = EquipmentNodeBuilder()
            .buildLoad()
            .withId(loadId)
            .withSubstationId("noId")
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(loadId)
                        .withLinks(listOf(transmissionLineSegment.ports[1].id))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    transmissionLineSegment.id to transmissionLineSegment,
                    load.id to load
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = transmissionLineSegment,
                targetPort = transmissionLineSegment.ports[0],
                linkId = powerSystemEquivalent.ports[0].id,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegment,
                sourcePort = transmissionLineSegment.ports[1],
                targetEquipment = load,
                targetPort = load.ports[0],
                linkId = transmissionLineSegment.ports[1].id,
                voltageLevelLibId = null
            )
            .build()

        EitherAssert.assertThat(
            requiredFieldsValidator.validate(scheme)
        ).containsLeftInstanceOf(
            EquipmentParamsValidationError.RequiredFieldNotFoundError::class.java
        )
    }
}
