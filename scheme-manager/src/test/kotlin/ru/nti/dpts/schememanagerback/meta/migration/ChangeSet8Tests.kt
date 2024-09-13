package ru.nti.dpts.schememanagerback.meta.migration

import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import ru.nti.dpts.schememanagerback.meta.migration.ops.loadChangeSets
import ru.nti.dpts.schememanagerback.meta.migration.ops.mutateScheme
import ru.nti.dtps.equipment.meta.info.mapper.ProtoJsonMapper
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.scheme.Scheme
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode

class ChangeSet8Tests {

    private val changeSets = loadChangeSets(loadChangeSetsContent()).filter { changeSet -> changeSet.id <= 8 }

    @Test
    fun `should add child default value`() {
        // given
        val scheme = Scheme.newBuilder().apply {
            metaSchemeChangeSetId = 7
            putNodes(
                "NODE_ID_1",
                EquipmentNode.newBuilder().apply {
                    libEquipmentId = EquipmentLibId.LOAD
                    putFields("SUBSTATION", "Awesome substation id")
                }.build()
            )
            putNodes(
                "NODE_ID_2",
                EquipmentNode.newBuilder().apply {
                    libEquipmentId = EquipmentLibId.SHORT_CIRCUIT
                    putFields("SUBSTATION", "Awesome substation id")
                }.build()
            )
            putNodes(
                "NODE_ID_3",
                EquipmentNode.newBuilder().apply {
                    libEquipmentId = EquipmentLibId.SHORT_CIRCUIT
                }.build()
            )
        }.build().let(ProtoJsonMapper::toJsonString).let(Document::parse)

        // when
        mutateScheme(scheme, changeSets)

        // then
        val nodes = (scheme[SchemeFieldsTest.nodes] as Document)

        assertThat((nodes["NODE_ID_1"] as Document)[SchemeFieldsTest.Node.fields])
            .hasFieldOrPropertyWithValue("SUBSTATION", "Awesome substation id")

        assertThat((nodes["NODE_ID_2"] as Document)[SchemeFieldsTest.Node.fields])
            .hasFieldOrPropertyWithValue("SUBSTATION", "Awesome substation id")

        assertThat((nodes["NODE_ID_3"] as Document)[SchemeFieldsTest.Node.fields])
            .hasFieldOrPropertyWithValue("SUBSTATION", "noId")
    }
}
