package ru.nti.dtps.cimconverter

import org.junit.jupiter.api.Assertions
import ru.nti.dtps.cimconverter.utils.getFileInputStream
import ru.nti.dtps.cimconverter.utils.readSchemeAsText
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.RawSchemeMapper
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.utils.graph.DtpsRawSchemeSearch
import kotlin.AssertionError
import kotlin.math.abs

fun convertToCimAndBackAndTest(path: String, testFieldValuesEqual: Boolean = true): Pair<String, String> {
    val initialSchemeString = readSchemeAsText(path)
    val cim: String = CimConverter.toCim(initialSchemeString)
    val convertedSchemeString = CimConverter.fromCim(cim.byteInputStream())

    val initialScheme = RawSchemeMapper.fromJsonString(initialSchemeString)
    val convertedScheme = RawSchemeMapper.fromJsonString(convertedSchemeString)

    assertPortsAmount(convertedScheme)
    assertEquipmentsLibIdAndVoltageId(initialScheme, convertedScheme)
    assertLinksAndPortsConsistency(convertedScheme)
    assertGraphIsConnected(convertedScheme)
    if (testFieldValuesEqual) {
        assertFieldValuesEqual(initialScheme, convertedScheme)
    }
    return initialSchemeString to convertedSchemeString
}

fun convertToSchemeAndTest(path: String): String {
    val convertedSchemeString = CimConverter.fromCim(getFileInputStream(path))

    val convertedScheme = RawSchemeMapper.fromJsonString(convertedSchemeString)

    assertPortsAmount(convertedScheme)
    assertLinksAndPortsConsistency(convertedScheme)
    assertGraphIsConnected(convertedScheme)
    return convertedSchemeString
}

private fun assertEquipmentsLibIdAndVoltageId(initialScheme: RawSchemeDto, convertedScheme: RawSchemeDto) {
    initialScheme.nodes.values
        .filter { it.libEquipmentId != EquipmentLibId.CONNECTIVITY }
        .forEach { initialEquipment ->
            val convertedEquipment = convertedScheme.nodes[initialEquipment.id]
            if (convertedEquipment == null) {
                throw AssertionError(
                    "Equipment ${initialEquipment.name()} was lost during two directional conversion"
                )
            } else {
                if (initialEquipment.libEquipmentId != convertedEquipment.libEquipmentId) {
                    throw AssertionError(
                        "Initial equipment ${initialEquipment.name()} has ${initialEquipment.libEquipmentId} libId," +
                            " but the converted one has ${convertedEquipment.libEquipmentId}"
                    )
                }
                if (convertedEquipment.libEquipmentId != EquipmentLibId.SHORT_CIRCUIT &&
                    convertedEquipment.libEquipmentId != EquipmentLibId.SHORT_CIRCUIT_1PH &&
                    convertedEquipment.libEquipmentId != EquipmentLibId.VOLTAGE_TRANSFORMER &&
                    initialEquipment.voltageLevelId != convertedEquipment.voltageLevelId
                ) {
                    throw AssertionError(
                        "Initial equipment ${initialEquipment.name()} has ${initialEquipment.voltageLevelId} voltage level," +
                            " but the converted one has ${convertedEquipment.voltageLevelId}"
                    )
                }
            }
        }
}

private fun assertGraphIsConnected(convertedScheme: RawSchemeDto) {
    val equipmentsAmount = convertedScheme.nodes.size
    val searchFromEquipment = convertedScheme.nodes.values.first()
    val foundEquipmentIds = mutableSetOf(searchFromEquipment.id)

    val bfs = DtpsRawSchemeSearch.getBreadthFirstSearch(convertedScheme)

    bfs.searchFromNode(searchFromEquipment, doOnSiblingNodeAndPort = { sibling, _ ->
        foundEquipmentIds.add(sibling.id)
    })

    val notFoundEquipmentNames = mutableListOf<String>()

    convertedScheme.nodes.values.forEach { e ->
        if (!foundEquipmentIds.contains(e.id)) {
            notFoundEquipmentNames += e.name()
        }
    }

    if (notFoundEquipmentNames.isNotEmpty()) {
        notFoundEquipmentNames.forEach(::println)
        throw AssertionError(
            "Graph is not connected: equipment(-s) $notFoundEquipmentNames has(-ve) not been found." +
                " Total amount is ${notFoundEquipmentNames.size}"
        )
    }
    Assertions.assertEquals(equipmentsAmount, foundEquipmentIds.size)
}

private fun assertLinksAndPortsConsistency(convertedScheme: RawSchemeDto) {
    convertedScheme.links.values.forEach { link ->
        val source = convertedScheme.nodes[link.source] ?: throw AssertionError(
            "Link ${link.id} has reference to an equipment ${link.source}, but the equipment isn't present in scheme"
        )

        val target = convertedScheme.nodes[link.target] ?: throw AssertionError(
            "Link ${link.id} has reference to an equipment ${link.target}, but the equipment isn't present in scheme"
        )

        if (source.ports.none { it.id == link.sourcePort }) {
            throw AssertionError(
                "Link ${link.id} has references to ${link.source} and port ${link.sourcePort}," +
                    " but the equipment doesn't have the port"
            )
        }

        if (target.ports.none { it.id == link.targetPort }) {
            throw AssertionError(
                "Link ${link.id} has references to ${link.target} and port ${link.targetPort}," +
                    " but the equipment doesn't have the port"
            )
        }
    }

    convertedScheme.nodes.values.forEach { equipment ->
        equipment.ports.forEach { port ->
            port.links.forEach { linkId ->
                if (!convertedScheme.links.containsKey(linkId)) {
                    throw AssertionError(
                        "Scheme doesn't contain link '$linkId', but equipment '${equipment.name()}' has reference to that link"
                    )
                }
            }
        }
    }
}

private fun assertPortsAmount(convertedScheme: RawSchemeDto) {
    convertedScheme.nodes.values
        .filter { it.libEquipmentId != EquipmentLibId.BUS && it.libEquipmentId != EquipmentLibId.CONNECTIVITY }
        .forEach { equipment ->
            val equipmentLib = EquipmentMetaInfoManager.getEquipmentLibById(equipment.libEquipmentId)
            equipmentLib.ports.forEach { portLib ->
                if (!equipment.ports.any { it.libId == portLib.libId }) {
                    throw AssertionError(
                        "Equipment ${equipment.name()} doesn't have port ${portLib.libId}"
                    )
                }
            }
            Assertions.assertEquals(equipmentLib.ports.size, equipment.ports.size)
        }
}

private fun assertFieldValuesEqual(initialScheme: RawSchemeDto, convertedScheme: RawSchemeDto) {
    initialScheme.nodes.values
        .filter { it.libEquipmentId != EquipmentLibId.CONNECTIVITY }
        .forEach { initialEquipment ->
            val convertedEquipment = convertedScheme.nodes[initialEquipment.id] ?: throw AssertionError(
                "Equipment ${initialEquipment.name()} was lost during two directional conversion"
            )

            initialEquipment.fields
                .filter { (fieldId, _) ->
                    fieldId != FieldLibId.TAP_CHANGER_INSTALLATION_WINDING &&
                        fieldId != FieldLibId.TAP_CHANGER_DEFAULT_POSITION &&
                        fieldId != FieldLibId.TAP_CHANGER_VOLTAGE_CHANGE &&
                        fieldId != FieldLibId.TAP_CHANGER_MAX_POSITION &&
                        fieldId != FieldLibId.TAP_CHANGER_MIN_POSITION
                }
                .forEach { (fieldId, initialField) ->
                    val convertedField = convertedEquipment.fields[fieldId] ?: throw AssertionError(
                        "Equipment ${convertedEquipment.name()} field $fieldId was lost during two directional conversion"
                    )
                    if (!doFieldsEqual(initialField!!, convertedField)) {
                        throw AssertionError(
                            "Equipment \"${convertedEquipment.name()}\" field $fieldId has value $convertedField, but it must be equal to $initialField"
                        )
                    }
                }
        }
}

private fun doFieldsEqual(initialField: String, convertedField: String): Boolean {
    return if (initialField.toDoubleOrNull() != null) {
        initialField.toDouble() equals convertedField.toDouble()
    } else {
        initialField == convertedField
    }
}

private infix fun Double.equals(other: Double) = if (this == 0.0) {
    other == 0.0
} else {
    (abs(this - other) / this) < 0.01
}
