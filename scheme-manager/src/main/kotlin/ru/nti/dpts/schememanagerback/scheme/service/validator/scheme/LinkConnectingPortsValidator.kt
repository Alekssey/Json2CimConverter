package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getNameOrId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

@Component
class LinkConnectingPortsValidator : AbstractSchemeValidator(Level.THIRD) {

    override fun validate(scheme: SchemeDomain): Either<SchemeConnectionValidationError, Unit> {
        return scheme
            .apply { validateAllLinksHasValidRefers(this).mapLeft { return it.left() } }
            .apply { validateAllPortsHasValidLinks(this).mapLeft { return it.left() } }
            .let { Unit.right() }
    }

    private fun validateAllPortsHasValidLinks(scheme: SchemeDomain): Either<SchemeConnectionValidationError.SchemeContainsBlankPort, Unit> {
        for (equipment in scheme.nodes.values) {
            for (port in equipment.ports) {
                if (port.links.isEmpty()) {
                    return SchemeConnectionValidationError.SchemeContainsBlankPort(equipment.id, equipment.getNameOrId()).left()
                }

                if (port.links.size > 1 && equipment.libEquipmentId != EquipmentLibId.CONNECTIVITY) {
                    throw IllegalArgumentException("Port ${port.id} has more than one links: ${port.links}")
                }

                if (scheme.links[port.links.first()] == null) {
                    throw IllegalArgumentException("Port ${port.id} has link ${port.links.first()} that is missing from the scheme")
                }
            }
        }
        return Unit.right()
    }

    private fun validateAllLinksHasValidRefers(scheme: SchemeDomain): Either<SchemeConnectionValidationError.SchemeContainsDisconnectedLinks, Unit> {
        for (link in scheme.links.values) {
            if (link.sourcePort.isBlank()) {
                return SchemeConnectionValidationError.SchemeContainsDisconnectedLinks.left()
            }

            if (scheme.nodes[link.source] == null) {
                throw IllegalArgumentException("Link ${link.id} has source ${link.source} that is missing from the scheme")
            }
            if (scheme.nodes[link.target] == null) {
                throw IllegalArgumentException("Link ${link.id} has target ${link.target} that is missing from the scheme")
            }

            if (!scheme.nodes[link.source]!!.ports.map { it.id }.contains(link.sourcePort)) {
                throw IllegalArgumentException("Link ${link.id} has source port ${link.sourcePort} that is missing from the scheme")
            }
            if (!scheme.nodes[link.target]!!.ports.map { it.id }.contains(link.targetPort)) {
                throw IllegalArgumentException("Link ${link.id} has target port ${link.targetPort} that is missing from the scheme")
            }
        }
        return Unit.right()
    }
}

sealed class SchemeConnectionValidationError : ValidationError {
    class SchemeContainsBlankPort(val equipmentId: String, val equipmentName: String) : SchemeConnectionValidationError()
    object SchemeContainsDisconnectedLinks : SchemeConnectionValidationError()
}
