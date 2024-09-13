package ru.nti.dpts.schememanagerback.validator

import org.springframework.context.annotation.ComponentScan

@ComponentScan(
    basePackages =
    [
        "ru.nti.dpts.schememanagerback.scheme.service.augmentation",
        "ru.nti.dpts.schememanagerback.scheme.service.validator"
    ]
)
class TestConfigurationToValidation
