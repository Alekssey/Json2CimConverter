package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.BreadthFirstSearch
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

@Component
class IslandsValidator : AbstractSchemeValidator(Level.FOURTH) {

    private val sourceLibIds = setOf(
        EquipmentLibId.POWER_SYSTEM_EQUIVALENT,
        EquipmentLibId.SYNCHRONOUS_GENERATOR,
        EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC
    )

    override fun validate(scheme: SchemeDomain): Either<SchemeContainsIslandsError, Unit> {
        val bfs = BreadthFirstSearch(scheme).also { bfs ->
            bfs.searchFromNode(scheme.nodes.values.first { sourceLibIds.contains(it.libEquipmentId) })
        }

        if (bfs.visitedNodeIds.size != scheme.nodes.values.size) {
            return SchemeContainsIslandsError.left()
        }
        return Unit.right()
    }
}

object SchemeContainsIslandsError : ValidationError
