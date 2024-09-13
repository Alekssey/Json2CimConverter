package ru.nti.dpts.schememanagerback.meta.migration

import org.bson.Document
import org.junit.jupiter.api.Test
import ru.nti.dpts.schememanagerback.meta.migration.ops.loadChangeSets
import ru.nti.dpts.schememanagerback.meta.migration.ops.mutateScheme

class ChangeSet4Tests {

    private val changeSets = loadChangeSets(loadChangeSetsContent()).filter { changeSet -> changeSet.id <= 4 }

    @Test
    fun `should replace value`() {
        val scheme = loadSchemeDoc("src/test/resources/meta/migration/changeSet1/SimpleScheme.json")

        (scheme[SchemeFieldsTest.nodes] as Document).values.forEach { node ->
            if ((node as Document)[SchemeFieldsTest.Node.libEquipmentId] == "CIRCUIT_BREAKER") {
                val fields = node[SchemeFieldsTest.Node.fields] as Document
                assert(fields.size == 21)
            }
        }

        mutateScheme(scheme, changeSets)

        (scheme[SchemeFieldsTest.nodes] as Document).values.forEach { node ->
            if ((node as Document)[SchemeFieldsTest.Node.libEquipmentId] == "CIRCUIT_BREAKER") {
                val fields = node[SchemeFieldsTest.Node.fields] as Document
                assert(fields.size == 1)
            }
        }
    }
}
