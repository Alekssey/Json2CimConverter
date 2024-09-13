package ru.nti.dpts.schememanagerback.meta.migration

import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import ru.nti.dpts.schememanagerback.meta.migration.ops.loadChangeSets
import ru.nti.dpts.schememanagerback.meta.migration.ops.mutateScheme

class ChangeSet5Tests {

    private val changeSets = loadChangeSets(loadChangeSetsContent()).filter { changeSet -> changeSet.id <= 5 }

    @Test
    fun `should move values to child node`() {
        // given
        val scheme = loadSchemeDoc("src/test/resources/meta/migration/changeSet1/SimpleScheme.json")

        // when
        mutateScheme(scheme, changeSets)

        // then
        (scheme[SchemeFieldsTest.nodes] as Document).values
            .map { node -> node as Document }
            .forEach { node ->
                val fields = node[SchemeFieldsTest.Node.fields] as Document

                assertThat(fields).hasFieldOrProperty("NAME")
                if (node[SchemeFieldsTest.Node.libEquipmentId] != "TRANSMISSION_LINE_SEGMENT") {
                    assertThat(fields).hasFieldOrProperty("SUBSTATION")
                } else {
                    assertThat(fields).hasFieldOrProperty("TRANSMISSION_LINE")
                }
            }
    }
}
