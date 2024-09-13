package ru.nti.dpts.schememanagerback.meta.migration

import org.bson.Document
import org.junit.jupiter.api.Test
import ru.nti.dpts.schememanagerback.meta.migration.ops.loadChangeSets
import ru.nti.dpts.schememanagerback.meta.migration.ops.mutateScheme

class ChangeSet3Tests {

    private val changeSets = loadChangeSets(loadChangeSetsContent()).filter { changeSet -> changeSet.id <= 3 }

    @Test
    fun `should migrate key`() {
        val scheme = loadSchemeDoc("src/test/resources/meta/migration/changeSet1/SimpleScheme.json")

        (scheme[SchemeFieldsTest.nodes] as Document).values.forEach { node ->
            if ((node as Document)[SchemeFieldsTest.Node.libEquipmentId] == "TWO_WINDING_POWER_TRANSFORMER") {
                val fields = node[SchemeFieldsTest.Node.fields] as Document
                assert(fields.containsKey("PRIMARY_WINDING_RATED_VOLTAGE"))
                assert(fields.containsKey("SECONDARY_WINDING_RATED_VOLTAGE"))
            }
        }

        mutateScheme(scheme, changeSets)

        (scheme[SchemeFieldsTest.nodes] as Document).values.forEach { node ->
            if ((node as Document)[SchemeFieldsTest.Node.libEquipmentId] == "TWO_WINDING_POWER_TRANSFORMER") {
                val fields = node[SchemeFieldsTest.Node.fields] as Document
                assert(fields.containsKey("FIRST_WINDING_RATED_VOLTAGE"))
                assert(fields.containsKey("SECOND_WINDING_RATED_VOLTAGE"))
            }
        }
    }

    @Test
    fun `Replace value migration test`() {
        val scheme = loadSchemeDoc("src/test/resources/meta/migration/changeSet1/SimpleScheme.json")

        (scheme[SchemeFieldsTest.nodes] as Document).values.forEach { node ->
            if ((node as Document)[SchemeFieldsTest.Node.libEquipmentId] == "TWO_WINDING_POWER_TRANSFORMER") {
                val fields = node[SchemeFieldsTest.Node.fields] as Document
                assert(fields["TAP_CHANGER_INSTALLATION_WINDING"] == "onPrimaryWinding")
            }
        }

        mutateScheme(scheme, changeSets)

        (scheme[SchemeFieldsTest.nodes] as Document).values.forEach { node ->
            if ((node as Document)[SchemeFieldsTest.Node.libEquipmentId] == "TWO_WINDING_POWER_TRANSFORMER") {
                val fields = node[SchemeFieldsTest.Node.fields] as Document
                assert(fields["TAP_CHANGER_INSTALLATION_WINDING"] == "onFirstWinding")
            }
        }
    }
}
