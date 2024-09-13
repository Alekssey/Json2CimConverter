package ru.nti.dpts.schememanagerback.scheme.service.ops

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentLinkDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain

fun EquipmentLinkDomain.getSiblingFromSchemeOf(scheme: SchemeDomain, node: EquipmentNodeDomain): EquipmentNodeDomain {
    return if (this.source == node.id) { this.target } else { this.source }
        .let { siblingId -> scheme.nodes[siblingId]!! }
}
