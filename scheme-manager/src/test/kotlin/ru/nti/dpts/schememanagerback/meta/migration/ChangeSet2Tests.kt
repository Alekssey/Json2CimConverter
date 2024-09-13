package ru.nti.dpts.schememanagerback.meta.migration

import org.bson.Document
import org.junit.jupiter.api.Test
import ru.nti.dpts.schememanagerback.meta.migration.ops.loadChangeSets
import ru.nti.dpts.schememanagerback.meta.migration.ops.mutateScheme

class ChangeSet2Tests {

    private val changeSets = loadChangeSets(loadChangeSetsContent()).filter { changeSet -> changeSet.id <= 2 }

    @Test
    fun `Replace key migration test`() {
        val scheme = loadSchemeDoc("src/test/resources/meta/migration/changeSet1/SimpleScheme.json")

        (scheme[SchemeFieldsTest.nodes] as Document).values.forEach { node ->
            if ((node as Document)[SchemeFieldsTest.Node.libEquipmentId] == "TRANSMISSION_LINE_SEGMENT") {
                val fields = node[SchemeFieldsTest.Node.fields] as Document
                assert(fields.containsKey("SUSCEPTANCE_PER_UNIT_POS_NEG_SEQ"))
            }
        }

        mutateScheme(scheme, changeSets)

        (scheme[SchemeFieldsTest.nodes] as Document).values.forEach { node ->
            if ((node as Document)[SchemeFieldsTest.Node.libEquipmentId] == "TRANSMISSION_LINE_SEGMENT") {
                val fields = node[SchemeFieldsTest.Node.fields] as Document
                assert(!fields.containsKey("SUSCEPTANCE_PER_UNIT_POS_NEG_SEQ"))
                assert(fields.containsKey("SUSCEPTANCE_PER_LENGTH_POS_NEG_SEQ"))
            }
        }
    }

    @Test
    fun `Remove entry migration test`() {
        val scheme = loadSchemeDoc("src/test/resources/meta/migration/changeSet1/SimpleScheme.json")

        mutateScheme(scheme, changeSets)

        assert(!scheme.containsKey(SchemeFieldsTest.id))
    }
}
