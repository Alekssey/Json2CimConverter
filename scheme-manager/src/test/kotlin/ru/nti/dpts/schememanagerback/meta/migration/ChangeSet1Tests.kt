package ru.nti.dpts.schememanagerback.meta.migration

import org.bson.Document
import org.junit.jupiter.api.Test
import ru.nti.dpts.schememanagerback.meta.migration.ops.loadChangeSets
import ru.nti.dpts.schememanagerback.meta.migration.ops.mutateScheme

class ChangeSet1Tests {

    private val changeSets = loadChangeSets(loadChangeSetsContent()).filter { changeSet -> changeSet.id == 1 }

    @Test
    fun `Replace value migration test`() {
        val scheme = loadSchemeDoc("src/test/resources/meta/migration/changeSet1/SimpleScheme.json")

        mutateScheme(scheme, changeSets)

        assert(
            (scheme[SchemeFieldsTest.nodes] as Document).values.any { node ->
                (node as Document)[SchemeFieldsTest.Node.libEquipmentId] == "LOAD"
            }
        )

        assert(
            (scheme[SchemeFieldsTest.nodes] as Document).values.any { node ->
                (node as Document)[SchemeFieldsTest.Node.libEquipmentId] == "TRANSMISSION_LINE_SEGMENT"
            }
        )
    }

    @Test
    fun `Remove entry migration test`() {
        val scheme = loadSchemeDoc("src/test/resources/meta/migration/changeSet1/SimpleScheme.json")

        mutateScheme(scheme, changeSets)

        assert(
            (scheme[SchemeFieldsTest.nodes] as Document).values.all { node ->
                (node as Document)[SchemeFieldsTest.Node.type] == null
            }
        )
    }
}
