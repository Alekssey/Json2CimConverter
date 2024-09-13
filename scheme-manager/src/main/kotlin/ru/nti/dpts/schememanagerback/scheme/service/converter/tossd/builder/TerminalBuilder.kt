package ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder

import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TTerminal

fun buildTerminal(cnContainer: ConnectivityNodeContainer) = cnContainer.connectivityNode.pathName.let { cnPath ->
    cnPath.split("/").let { cnPathParts ->
        TTerminal().apply {
            connectivityNode = cnPath
            substationName = cnPathParts[0]
            voltageLevelName = cnPathParts[1]
            bayName = cnPathParts[2]
            cNodeName = cnPathParts[3]
        }
    }
}
