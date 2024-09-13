package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.Terminal

internal fun convertConnectivityNodeOfOneTerminal(
    terminals: List<Terminal>,
    objectIdToDiagramObjectMap: Map<String, DiagramObject>
): LinksCreationResult {
    val firstTerminal = terminals.first()
    val firstPort = createPortInfo(firstTerminal, objectIdToDiagramObjectMap)
    return LinksCreationResult(
        links = emptyList(),
        ports = listOf(firstPort)
    )
}
