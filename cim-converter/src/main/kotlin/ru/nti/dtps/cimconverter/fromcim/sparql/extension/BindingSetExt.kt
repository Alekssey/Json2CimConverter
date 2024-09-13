package ru.nti.dtps.cimconverter.fromcim.sparql.extension

import org.eclipse.rdf4j.query.BindingSet
import org.eclipse.rdf4j.sail.memory.model.MemIRI
import ru.nti.dtps.cimconverter.fromcim.exception.CimDataException
import ru.nti.dtps.cimconverter.fromcim.sparql.SUBJECT_NAME_OF_TRIPLE
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.CimField

fun BindingSet.extractIdentifiedObjectId(): String {
    val binding = getBinding(SUBJECT_NAME_OF_TRIPLE)
    return (binding.value as MemIRI).localName.removePrefix("_")
}

fun BindingSet.extractIdentifiedObjectNameOrNull(
    variable: CimField = CimClasses.IdentifiedObject.name
): String? {
    return if (hasBinding(variable.nameWithNamespaceAlias)) {
        val binding = getBinding(variable.nameWithNamespaceAlias)
        binding.value.stringValue()
    } else {
        null
    }
}

fun BindingSet.extractObjectReferenceOrNull(variable: CimField): String? {
    return if (hasBinding(variable.nameWithNamespaceAlias)) {
        val binding = getBinding(variable.nameWithNamespaceAlias)
        (binding.value as MemIRI).localName.removePrefix("_")
    } else {
        null
    }
}

fun BindingSet.extractIntValueOrNull(variable: CimField): Int? {
    val value = extractStringValueOrNull(variable)
    return try {
        value?.toDouble()?.toInt()
    } catch (e: NumberFormatException) {
        throw CimDataException("Expected integer value, found $value")
    }
}

fun BindingSet.extractDoubleValueOrNull(variable: CimField): Double? {
    val value = extractStringValueOrNull(variable)
    return try {
        value?.toDouble()
    } catch (e: NumberFormatException) {
        throw CimDataException("Expected integer value, found $value")
    }
}

fun BindingSet.extractBooleanValueOrFalse(variable: CimField): Boolean {
    val value = extractStringValueOrNull(variable)
    return value?.toBoolean() ?: false
}

fun BindingSet.extractStringValueOrNull(variable: CimField): String? {
    return if (hasBinding(variable.nameWithNamespaceAlias)) {
        val binding = getBinding(variable.nameWithNamespaceAlias)
        binding.value.stringValue()
    } else {
        null
    }
}
