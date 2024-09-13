package ru.nti.dpts.schememanagerback.meta.migration

import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import ru.nti.dpts.schememanagerback.meta.migration.ops.loadChangeSets
import ru.nti.dpts.schememanagerback.meta.migration.ops.mutateScheme

class ChangeSet7Tests {

    private val changeSets = loadChangeSets(loadChangeSetsContent()).filter { changeSet -> changeSet.id <= 7 }

    @Test
    fun `should remove fields of CONNECTIVITY node`() {
        // given
        val scheme = loadSchemeDoc("src/test/resources/meta/migration/changeSet7/ComplicatedScheme.json")

        // when
        mutateScheme(scheme, changeSets)

        // then
        (scheme[SchemeFieldsTest.nodes] as Document).values
            .map { node -> node as Document }
            .filter { node -> node[SchemeFieldsTest.Node.libEquipmentId] == "CONNECTIVITY" }
            .also { connectivityNodes -> assertThat(connectivityNodes).isNotEmpty }
            .forEach { connectivityNode ->
                val fields = connectivityNode[SchemeFieldsTest.Node.fields] as Document

                assertThat(fields).isEmpty()
            }

        (scheme[SchemeFieldsTest.nodes] as Document).values
            .map { node -> node as Document }
            .filter { node -> node[SchemeFieldsTest.Node.libEquipmentId] != "CONNECTIVITY" }
            .also { anotherNodes -> assertThat(anotherNodes).isNotEmpty }
            .forEach { anotherNode ->
                val fields = anotherNode[SchemeFieldsTest.Node.fields] as Document

                assertThat(fields).hasFieldOrProperty("NAME")

                if (anotherNode[SchemeFieldsTest.Node.libEquipmentId] != "TRANSMISSION_LINE_SEGMENT") {
                    assertThat(fields).hasFieldOrProperty("SUBSTATION")
                } else {
                    assertThat(fields).hasFieldOrProperty("TRANSMISSION_LINE")
                }
            }
    }
}
