package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme

abstract class AbstractSchemeValidator(private val level: Level) : SchemeValidator {
    /**
     * Level value is used to indicate the order in which validation is applied
     */
    enum class Level(val number: Int) {
        ZERO(0),
        FIRST(1),
        SECOND(2),
        THIRD(3),
        FOURTH(4)
    }

    override fun getLevelNumber() = level.number
}
