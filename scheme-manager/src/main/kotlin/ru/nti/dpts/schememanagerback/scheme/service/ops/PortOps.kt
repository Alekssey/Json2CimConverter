package ru.nti.dpts.schememanagerback.scheme.service.ops

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentLinkDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain

fun PortDto.getLinksFromScheme(scheme: SchemeDomain): List<EquipmentLinkDomain> {
    return scheme.links.filterKeys { it in this.links }.values.toList()
}
