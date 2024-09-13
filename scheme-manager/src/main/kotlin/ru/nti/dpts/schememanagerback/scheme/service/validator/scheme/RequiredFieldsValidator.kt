package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getFieldValueOrDefault
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getFieldValueOrNull
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getName
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getNameOrId
import ru.nti.dtps.equipment.meta.info.dataclass.common.Language
import ru.nti.dtps.equipment.meta.info.dataclass.equipment.field.FieldLib
import ru.nti.dtps.equipment.meta.info.dataclass.equipment.field.FieldType
import ru.nti.dtps.equipment.meta.info.dataclass.equipment.field.FieldValueType
import ru.nti.dtps.equipment.meta.info.extension.isConnectivity
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import java.util.regex.Pattern
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

@Component
class RequiredFieldsValidator : AbstractSchemeValidator(Level.SECOND) {

    companion object {
        private val logger = LoggerFactory.getLogger(RequiredFieldsValidator::class.java)

        fun validateFields(
            equipment: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            checkEquipmentNameExistence(equipment).mapLeft { return it.left() }

            EquipmentMetaInfoManager.getEquipmentLibById(equipment.libEquipmentId).fields
                .filter { it.id != FieldLibId.SHOULD_FREQUENCY_BE_MEASURED } // remove
                .let { fieldsLib ->
                    checkIfThereAreNotUnexpectedFields(equipment, fieldsLib)
                    checkRequiredFieldsExistenceAndItsValue(equipment, fieldsLib.filter { !it.optional }).mapLeft {
                        return it.left()
                    }
                }

            when (equipment.libEquipmentId) {
                EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER,
                EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER,
                EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER,
                EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER -> {
                    checkRequiredFieldsTransformerIfRpnEnabled(equipment).mapLeft { return it.left() }
                    checkRequiredMagneticCouplingTransformer(equipment).mapLeft { return it.left() }
                }

                EquipmentLibId.TRANSMISSION_LINE_SEGMENT,
                EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT -> {
                    checkRequiredFieldsTransmissionLine(equipment).mapLeft { return it.left() }
                    checkRequiredFieldsForUseConcentratedParameters(equipment).mapLeft { return it.left() }
                }

                EquipmentLibId.SYNCHRONOUS_GENERATOR ->
                    checkSynchronousGeneratorFields(equipment).mapLeft { return it.left() }

                else -> Unit
            }
            return Unit.right()
        }

        private fun checkRequiredFieldsTransformerIfRpnEnabled(
            transformer: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            if (transformer.fields[FieldLibId.TAP_CHANGER_EXISTENCE] == "enabled") {
                val max = transformer.fields[FieldLibId.TAP_CHANGER_MAX_POSITION]!!.toDouble()
                val min = transformer.fields[FieldLibId.TAP_CHANGER_MIN_POSITION]!!.toDouble()
                val default = transformer.fields[FieldLibId.TAP_CHANGER_DEFAULT_POSITION]!!.toDouble()
                val voltageChange = transformer.fields[FieldLibId.TAP_CHANGER_VOLTAGE_CHANGE]!!.toDouble()

                if (min > max || default > max || default < min || voltageChange > max || voltageChange < min) {
                    return EquipmentParamsValidationError.PowerTransformerRpnValuesError(
                        transformer.id,
                        transformer.getName()
                    ).left()
                }
            }
            return Unit.right()
        }

        private fun checkRequiredMagneticCouplingTransformer(
            transformer: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            return when (transformer.libEquipmentId) {
                EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER,
                EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER ->
                    checkMathMagneticCouplingTwoPowerTransformer(transformer)

                EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER,
                EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER ->
                    checkMathMagneticCouplingThreePowerTransformer(transformer)

                else -> Unit.right()
            }
        }

        private fun checkMathMagneticCouplingTwoPowerTransformer(
            transformer: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            val requiredMagneticCoupling = 10.0.pow(-9.0)

            val f = transformer.fields[FieldLibId.FREQUENCY]!!.toDouble()
            val uk = transformer.fields[FieldLibId.SHORT_CIRCUIT_VOLTAGE]!!.toDouble()
            val i0 = transformer.fields[FieldLibId.IDLING_CURRENT]!!.toDouble()
            val u1 = transformer.fields[FieldLibId.FIRST_WINDING_RATED_VOLTAGE]!!.toDouble()
            val u2 = transformer.fields[FieldLibId.SECOND_WINDING_RATED_VOLTAGE]!!.toDouble()
            val s = transformer.fields[FieldLibId.RATED_APPARENT_POWER]!!.toDouble()

            val resultMagneticCoupling = 1 - (
                ((1 / (2 * PI * f)) * ((100 / i0) - (0.5 * 0.01 * uk)) * ((u1 * u2) / s)) /
                    sqrt((1 / (2 * PI * f)).pow(2) * (100 / i0).pow(2) * (u1.pow(2) / s) * (u2.pow(2) / s))
                )

            return if (resultMagneticCoupling < requiredMagneticCoupling) {
                EquipmentParamsValidationError.PowerTransformerMagneticCouplingError(
                    transformer.id,
                    transformer.getName()
                ).left()
            } else {
                Unit.right()
            }
        }

        private fun checkMathMagneticCouplingThreePowerTransformer(
            transformer: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            val requiredMagneticCoupling = 10.0.pow(-9.0)

            val f = transformer.fields[FieldLibId.FREQUENCY]!!.toDouble()
            val ukFirstSecond = transformer.fields[FieldLibId.FIRST_SECOND_WINDING_SHORT_CIRCUIT_VOLTAGE]!!.toDouble()
            val ukFirstThird = transformer.fields[FieldLibId.FIRST_THIRD_WINDING_SHORT_CIRCUIT_VOLTAGE]!!.toDouble()
            val ukSecondThird = transformer.fields[FieldLibId.SECOND_THIRD_WINDING_SHORT_CIRCUIT_VOLTAGE]!!.toDouble()
            val i0 = transformer.fields[FieldLibId.IDLING_CURRENT]!!.toDouble()
            val u1 = transformer.fields[FieldLibId.FIRST_WINDING_RATED_VOLTAGE]!!.toDouble()
            val u2 = transformer.fields[FieldLibId.SECOND_WINDING_RATED_VOLTAGE]!!.toDouble()
            val u3 = transformer.fields[FieldLibId.THIRD_WINDING_RATED_VOLTAGE]!!.toDouble()
            val s = transformer.fields[FieldLibId.RATED_APPARENT_POWER]!!.toDouble()

            val resultMagneticCouplingFirstAndSecondWind = 1 - (
                (
                    (1 / (2 * PI * f)) *
                        ((100 / i0) - (0.5 * 0.01 * ukFirstSecond)) * ((u1 * u2) / s)
                    ) /
                    sqrt((1 / (2 * PI * f)).pow(2) * (100 / i0).pow(2) * (u1.pow(2) / s) * (u2.pow(2) / s))
                )

            val resultMagneticCouplingFirstAndThirdWind = 1 - (
                (
                    (1 / (2 * PI * f)) *
                        ((100 / i0) - (0.5 * 0.01 * ukFirstThird)) * ((u1 * u3) / s)
                    ) /
                    sqrt((1 / (2 * PI * f)).pow(2) * (100 / i0).pow(2) * (u1.pow(2) / s) * (u3.pow(2) / s))
                )

            val resultMagneticCouplingSecondAndThirdWind = 1 - (
                (
                    (1 / (2 * PI * f)) *
                        ((100 / i0) - (0.5 * 0.01 * ukSecondThird)) * ((u2 * u3) / s)
                    ) /
                    sqrt((1 / (2 * PI * f)).pow(2) * (100 / i0).pow(2) * (u2.pow(2) / s) * (u3.pow(2) / s))
                )

            return if (resultMagneticCouplingFirstAndSecondWind < requiredMagneticCoupling ||
                resultMagneticCouplingFirstAndThirdWind < requiredMagneticCoupling ||
                resultMagneticCouplingSecondAndThirdWind < requiredMagneticCoupling
            ) {
                EquipmentParamsValidationError.PowerTransformerMagneticCouplingError(
                    transformer.id,
                    transformer.getName()
                ).left()
            } else {
                Unit.right()
            }
        }

        private fun checkRequiredFieldsTransmissionLine(
            transmissionLine: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            val resistanceZero = transmissionLine.fields[FieldLibId.RESISTANCE_PER_LENGTH_ZERO_SEQ]!!.toDouble()
            val resistancePos = transmissionLine.fields[FieldLibId.RESISTANCE_PER_LENGTH_POS_NEG_SEQ]!!.toDouble()
            val reactanceZero = transmissionLine.fields[FieldLibId.REACTANCE_PER_LENGTH_ZERO_SEQ]!!.toDouble()
            val reactancePos = transmissionLine.fields[FieldLibId.REACTANCE_PER_LENGTH_POS_NEG_SEQ]!!.toDouble()
            val susceptanceZero = transmissionLine.fields[FieldLibId.SUSCEPTANCE_PER_LENGTH_ZERO_SEQ]!!.toDouble()
            val susceptancePos = transmissionLine.fields[FieldLibId.SUSCEPTANCE_PER_LENGTH_POS_NEG_SEQ]!!.toDouble()

            return if (resistanceZero < resistancePos || reactanceZero < reactancePos || susceptancePos < susceptanceZero) {
                EquipmentParamsValidationError.LineSegmentPreconditionParamsError(
                    transmissionLine.id,
                    transmissionLine.getName()
                ).left()
            } else {
                Unit.right()
            }
        }

        private fun checkRequiredFieldsForUseConcentratedParameters(
            transmissionLine: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            return if (transmissionLine.fields[FieldLibId.USE_CONCENTRATED_PARAMETERS] == "disabled" &&
                checkTransmissionLineFieldsIsValid(transmissionLine)
            ) {
                EquipmentParamsValidationError.LineSegmentPreconditionConcentratedParamsError(
                    transmissionLine.id,
                    transmissionLine.getName()
                ).left()
            } else {
                Unit.right()
            }
        }

        private fun checkTransmissionLineFieldsIsValid(transmissionLine: EquipmentNodeDomain): Boolean {
            val frequency = transmissionLine.fields[FieldLibId.FREQUENCY]!!.toDouble()
            val length = transmissionLine.fields[FieldLibId.LENGTH]!!.toDouble()
            val reactance = transmissionLine.fields[FieldLibId.REACTANCE_PER_LENGTH_POS_NEG_SEQ]!!.toDouble()
            val susceptance = transmissionLine.fields[FieldLibId.SUSCEPTANCE_PER_LENGTH_POS_NEG_SEQ]!!.toDouble()

            val inductance = 2 * PI * frequency * length * reactance
            val reactivity = 1 / (2 * PI * frequency * length * susceptance)
            val tMode = sqrt(inductance * reactivity)
            val calculationStep = 50.0 * 10.0.pow(-6.0)

            return tMode < calculationStep
        }

        private fun checkSynchronousGeneratorFields(
            synchronousGenerator: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            val upperLimit = synchronousGenerator.fields[FieldLibId.FLAP_STATE_UPPER_LIMIT]!!.toDouble()
            val lowerLimit = synchronousGenerator.fields[FieldLibId.FLAP_STATE_LOWER_LIMIT]!!.toDouble()
            return if (lowerLimit > upperLimit) {
                EquipmentParamsValidationError.GeneratorPreconditionParamsError(
                    synchronousGenerator.id,
                    synchronousGenerator.getName()
                ).left()
            } else {
                Unit.right()
            }
        }

        private fun checkEquipmentNameExistence(
            equipment: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            if (!equipment.libEquipmentId.isConnectivity() && equipment.getName().isBlank()) {
                return EquipmentParamsValidationError.EquipmentDoesNotHaveNameError(
                    equipment.id
                ).left()
            }
            return Unit.right()
        }

        private fun checkIfThereAreNotUnexpectedFields(
            equipment: EquipmentNodeDomain,
            fieldsLib: List<FieldLib>
        ) {
            val unexpectedFields = equipment.fields.keys.filter { fieldLibId ->
                !fieldsLib.any { it.id == fieldLibId } && fieldLibId != FieldLibId.SHOULD_FREQUENCY_BE_MEASURED
            }
            if (unexpectedFields.isNotEmpty()) {
                throw IllegalArgumentException(
                    "Equipment ${equipment.getNameOrId()} (${equipment.libEquipmentId}) contains unexpected field(-s) $unexpectedFields"
                )
            }
        }

        private fun checkRequiredFieldsExistenceAndItsValue(
            equipment: EquipmentNodeDomain,
            fieldsLib: List<FieldLib>
        ): Either<EquipmentParamsValidationError, Unit> {
            fieldsLib.forEach { fieldLib ->
                if (fieldLib.enabledIf == null) {
                    checkRequiredFieldExistenceAndItsValue(equipment, fieldLib).mapLeft { return it.left() }
                } else if (
                    fieldLib.enabledIf!!.enablingValues.contains(
                        equipment.getFieldValueOrDefault(fieldLib.enabledIf!!.fieldLibId)
                    )
                ) {
                    checkRequiredFieldExistenceAndItsValue(equipment, fieldLib).mapLeft { return it.left() }
                }
            }
            return Unit.right()
        }

        private fun checkRequiredFieldExistenceAndItsValue(
            equipment: EquipmentNodeDomain,
            fieldLib: FieldLib
        ): Either<EquipmentParamsValidationError, Unit> {
            val fieldValueString = equipment.getFieldValueOrNull(fieldLib.id)
            return if (fieldValueString.isNullOrBlank()) {
                EquipmentParamsValidationError.RequiredFieldNotFoundError(
                    equipment.id,
                    equipment.getNameOrId(),
                    fieldLib
                ).left()
            } else {
                validateFieldValueIfExistsByFieldType(equipment, fieldLib, fieldValueString)
            }
        }

        private fun validateFieldValueIfExistsByFieldType(
            equipment: EquipmentNodeDomain,
            fieldLib: FieldLib,
            fieldValueString: String
        ): Either<EquipmentParamsValidationError, Unit> {
            EquipmentMetaInfoManager.getFieldTypeById(fieldLib.typeId).let { fieldType ->
                when (fieldType.valueType) {
                    FieldValueType.NUMBER -> return validateNumber(
                        fieldValueString,
                        fieldLib,
                        fieldType,
                        equipment
                    )

                    FieldValueType.TEXT -> return validateText(
                        fieldValueString,
                        fieldLib,
                        fieldType,
                        equipment
                    )

                    FieldValueType.SELECT, FieldValueType.TOGGLE, FieldValueType.CHECKBOX -> validateFieldWithOptions(
                        fieldValueString,
                        fieldLib,
                        fieldType,
                        equipment
                    )

                    FieldValueType.LINE_TEMPLATE_SELECT -> logger.trace(
                        "Fields with type LINE_TEMPLATE_SELECT don't be validated," +
                            " because its validation requires external data base calls"
                    )
                    FieldValueType.SUBSTATION_SELECT -> logger.trace(
                        "Fields with type LINE_TEMPLATE_SELECT don't be validated," +
                            " because its validation requires external data base calls"
                    )
                    FieldValueType.TRANSMISSION_LINE_SELECT -> logger.trace(
                        "Fields with type LINE_TEMPLATE_SELECT don't be validated," +
                            " because its validation requires external data base calls"
                    )
                }
            }
            return Unit.right()
        }

        private fun validateText(
            fieldValueString: String,
            fieldLib: FieldLib,
            fieldType: FieldType,
            equipment: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            if (fieldType.length != null && fieldType.length != 0 && fieldType.length!! != fieldValueString.length) {
                return EquipmentParamsValidationError.TextFieldLengthDoNotEqualValueError(
                    equipment.id,
                    equipment.getNameOrId(),
                    fieldLib,
                    fieldType.length!!
                ).left()
            }

            if (fieldType.maxLength != null && fieldType.maxLength != 0 && fieldType.maxLength!! < fieldValueString.length) {
                return EquipmentParamsValidationError.TextFieldLengthMoreThenMaxValueError(
                    equipment.id,
                    equipment.getNameOrId(),
                    fieldLib,
                    fieldType.maxLength!! + 1
                ).left()
            }

            if (fieldType.minLength != null && fieldType.minLength != 0 && fieldType.minLength!! > fieldValueString.length) {
                return EquipmentParamsValidationError.TextFieldLessThenMinValueError(
                    equipment.id,
                    equipment.getNameOrId(),
                    fieldLib,
                    fieldType.minLength!! - 1
                ).left()
            }

            if (fieldType.regex != null && !Pattern.compile(fieldType.regex).matcher(fieldValueString).matches()) {
                throw IllegalArgumentException(
                    "Equipment ${equipment.getNameOrId()} (${equipment.libEquipmentId}) has field ${fieldLib.name[Language.RU]} " +
                        "with value $fieldValueString that doesn't match pattern: ${fieldType.regex}}"
                )
            }
            return Unit.right()
        }

        private fun validateFieldWithOptions(
            fieldValueString: String,
            fieldLib: FieldLib,
            fieldType: FieldType,
            equipment: EquipmentNodeDomain
        ) {
            if (!fieldType.options.any { option -> option.key == fieldValueString }) {
                throw IllegalArgumentException(
                    "Equipment ${equipment.getNameOrId()} (${equipment.libEquipmentId}) has field ${fieldLib.name[Language.RU]} " +
                        "with value $fieldValueString, but expected ${fieldType.options.map { it.key }}"
                )
            }
        }

        private fun validateNumber(
            fieldValueString: String,
            fieldLib: FieldLib,
            fieldType: FieldType,
            equipment: EquipmentNodeDomain
        ): Either<EquipmentParamsValidationError, Unit> {
            val fieldValueDouble = try {
                fieldValueString.toDouble()
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException(
                    "Equipment ${equipment.getNameOrId()} (${equipment.libEquipmentId}) has field ${fieldLib.name[Language.RU]} " +
                        "with value $fieldValueString that must be a number"
                )
            }

            val max = (fieldLib.max ?: fieldType.max)!!
            val min = (fieldLib.min ?: fieldType.min)!!

            if (fieldValueDouble > max) {
                return EquipmentParamsValidationError.FieldValueMoreThenLimitValueError(
                    equipment.id,
                    equipment.getNameOrId(),
                    fieldLib,
                    max
                ).left()
            } else if (fieldValueDouble < min) {
                return EquipmentParamsValidationError.FieldValueLessThenLimitValueError(
                    equipment.id,
                    equipment.getNameOrId(),
                    fieldLib,
                    min
                ).left()
            }
            return Unit.right()
        }
    }

    override fun validate(scheme: SchemeDomain): Either<EquipmentParamsValidationError, Unit> {
        scheme.nodes.values.forEach { validateFields(it).mapLeft { error -> return error.left() } }
        scheme.dashboardNodes.values.forEach { validateFields(it).mapLeft { error -> return error.left() } }
        return Unit.right()
    }
}

sealed class EquipmentParamsValidationError : ValidationError {
    class FieldValueLessThenLimitValueError(
        val equipmentId: String,
        val equipmentName: String,
        val fieldLib: FieldLib,
        val boundValue: Double
    ) : EquipmentParamsValidationError()

    class FieldValueMoreThenLimitValueError(
        val equipmentId: String,
        val equipmentName: String,
        val fieldLib: FieldLib,
        val boundValue: Double
    ) : EquipmentParamsValidationError()

    class TextFieldLengthDoNotEqualValueError(
        val equipmentId: String,
        val equipmentName: String,
        val fieldLib: FieldLib,
        val boundValue: Int
    ) : EquipmentParamsValidationError()

    class TextFieldLengthMoreThenMaxValueError(
        val equipmentId: String,
        val equipmentName: String,
        val fieldLib: FieldLib,
        val boundValue: Int
    ) : EquipmentParamsValidationError()

    class TextFieldLessThenMinValueError(
        val equipmentId: String,
        val equipmentName: String,
        val fieldLib: FieldLib,
        val boundValue: Int
    ) : EquipmentParamsValidationError()

    class RequiredFieldNotFoundError(
        val equipmentId: String,
        val equipmentName: String,
        val fieldLib: FieldLib
    ) : EquipmentParamsValidationError()

    class EquipmentDoesNotHaveNameError(
        val equipmentId: String
    ) : EquipmentParamsValidationError()

    class GeneratorPreconditionParamsError(
        val equipmentId: String,
        val equipmentName: String
    ) : EquipmentParamsValidationError()

    class LineSegmentPreconditionConcentratedParamsError(
        val equipmentId: String,
        val equipmentName: String
    ) : EquipmentParamsValidationError()

    class LineSegmentPreconditionParamsError(
        val equipmentId: String,
        val equipmentName: String
    ) : EquipmentParamsValidationError()

    class PowerTransformerRpnValuesError(
        val equipmentId: String,
        val equipmentName: String
    ) : EquipmentParamsValidationError()

    class PowerTransformerMagneticCouplingError(
        val equipmentId: String,
        val equipmentName: String
    ) : EquipmentParamsValidationError()

    class LineTemplateNotSelected(
        val equipmentId: String,
        val equipmentName: String
    ) : EquipmentParamsValidationError()
}
