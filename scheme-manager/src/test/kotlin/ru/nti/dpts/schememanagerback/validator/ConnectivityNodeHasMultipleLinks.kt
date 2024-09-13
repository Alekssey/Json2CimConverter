package ru.nti.dpts.schememanagerback.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.nti.dpts.schememanagerback.builder.SchemesForTest
import ru.nti.dpts.schememanagerback.scheme.service.augmentation.AugmentationService
import ru.nti.dpts.schememanagerback.scheme.service.validator.ValidatorService
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

@SpringBootTest(classes = [TestConfigurationToValidation::class])
class ConnectivityNodeHasMultipleLinks {

    @Autowired
    lateinit var validatorService: ValidatorService

    @Autowired
    lateinit var augmentationService: AugmentationService

    @Test
    fun portOfConnectivityNodeCanContainsMultipleLinks() {
        val scheme = SchemesForTest.buildSchemeWithAllTypesEquipment()
        validatorService.validate(scheme)
        augmentationService.augment(scheme)

        scheme.nodes.values.filter { node -> node.libEquipmentId == EquipmentLibId.CONNECTIVITY }
            .forEach { connectivity ->
                assertThat(connectivity.voltageLevelId != null)
            }
    }
}
