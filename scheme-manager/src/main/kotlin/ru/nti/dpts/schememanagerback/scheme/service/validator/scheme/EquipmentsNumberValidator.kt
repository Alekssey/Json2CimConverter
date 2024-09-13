package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain

@Component
class EquipmentsNumberValidator(
    @Value("\${app.allowed-equipment-number}")
    private val allowedEquipmentNumber: Int
) : AbstractSchemeValidator(Level.ZERO) {

    override fun validate(scheme: SchemeDomain): Either<EquipmentCountIsMoreThenLimitError, Unit> {
        val equipmentsNumber = scheme.nodes.values.size
        return if (equipmentsNumber > allowedEquipmentNumber) {
            EquipmentCountIsMoreThenLimitError(
                allowedEquipmentNumber,
                equipmentsNumber
            ).left()
        } else {
            Unit.right()
        }
    }
}

class EquipmentCountIsMoreThenLimitError(
    val allowedEquipmentNumber: Int,
    val currentEquipmentNumber: Int
) : ValidationError

/*
* throw ValidationFailure(
                messageSourceService.getMessage("api.scheme.validation.error.equipments.number")
                    .format(equipmentsNumber, allowedEquipmentNumber)
            )
* */
