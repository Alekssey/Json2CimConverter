/*
package ru.nti.dpts.schememanagerback.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.builder.SubstationBuilder
import ru.nti.dpts.schememanagerback.manager.SubstationManager
import ru.nti.dpts.schememanagerback.model.Project
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import java.util.*

class DeleteSubstationManagerTest {
    private val PROJECT_ID: String = "TestProjectId_1"
    private val FIRST_SUBSTATION_ID: String = "Substation_Test_1"
    private val SECOND_SUBSTATION_ID: String = "Substation_Test_2"

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private val projectServiceMock = Mockito.mock(ProjectService::class.java)
    private val messageSourceServiceMock = Mockito.mock(MessageSourceService::class.java)
    private val substationManager = SubstationManager(projectServiceMock, messageSourceServiceMock)

    private val substation1 = SubstationBuilder()
        .withId(FIRST_SUBSTATION_ID)
        .withName("Substation-1")
        .build()
    private val substation2 = SubstationBuilder()
        .withId(SECOND_SUBSTATION_ID)
        .withName("Substation-2")
        .build()

    private val substations = mutableMapOf(
        FIRST_SUBSTATION_ID to substation1,
        SECOND_SUBSTATION_ID to substation2
    )
    private val substationAmount = substations.size

    @Test
    fun `check locking of deleting substation`() {
        val equipmentNodeDtoFirst = EquipmentNodeBuilder()
            .withVoltageLevelId(VoltageLevelLibId.VOLTS_12)
            .withSubstationId(FIRST_SUBSTATION_ID)
            .withLibEquipmentId(EquipmentLibId.BUS)
            .withFields(mapOf(FieldLibId.FIRST_WINDING_RATED_VOLTAGE to "115"))
            .build()

        val equipmentNodeDtoSecond = EquipmentNodeBuilder()
            .withVoltageLevelId(VoltageLevelLibId.VOLTS_12)
            .withSubstationId(SECOND_SUBSTATION_ID)
            .withLibEquipmentId(EquipmentLibId.BUS)
            .withFields(mapOf(FieldLibId.FIRST_WINDING_RATED_VOLTAGE to "115"))
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    equipmentNodeDtoFirst.id to equipmentNodeDtoFirst,
                    equipmentNodeDtoSecond.id to equipmentNodeDtoSecond
                )
            )
            .withSubstations(listOf(substation1, substation2))
            .build()

        val project = Project(PROJECT_ID, "", Date(), true, scheme)
        Mockito.`when`(projectServiceMock.getProject(PROJECT_ID)).thenReturn(project)

        Mockito.`when`(messageSourceServiceMock.getMessage("api.substation.error.contains.elements"))
            .thenReturn("Substation with name \"%s\" contains equipment")

        assertThrows<IllegalArgumentException> {
            substationManager.deleteSubstationIfEmpty(PROJECT_ID, FIRST_SUBSTATION_ID)
        }
        Assertions.assertThat(substations.size).isEqualTo(substationAmount)
    }

    @Test
    fun `check deleting substation`() {
        val equipmentNodeDtoFirst = EquipmentNodeBuilder()
            .withVoltageLevelId(VoltageLevelLibId.VOLTS_12)
            .withSubstationId(SECOND_SUBSTATION_ID)
            .withLibEquipmentId(EquipmentLibId.BUS)
            .withFields(mapOf(FieldLibId.FIRST_WINDING_RATED_VOLTAGE to "115"))
            .build()

        val equipmentNodeDtoSecond = EquipmentNodeBuilder()
            .withVoltageLevelId(VoltageLevelLibId.VOLTS_12)
            .withSubstationId(SECOND_SUBSTATION_ID)
            .withLibEquipmentId(EquipmentLibId.BUS)
            .withFields(mapOf(FieldLibId.FIRST_WINDING_RATED_VOLTAGE to "115"))
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    equipmentNodeDtoFirst.id to equipmentNodeDtoFirst,
                    equipmentNodeDtoSecond.id to equipmentNodeDtoSecond
                )
            )
            .withSubstations(listOf(substation1, substation2))
            .build()

        val project = Project(PROJECT_ID, "", Date(), true, scheme)
        Mockito.`when`(projectServiceMock.getProject(PROJECT_ID)).thenReturn(project)

        Mockito.`when`(messageSourceServiceMock.getMessage("api.substation.error.contains.elements"))
            .thenReturn("Substation with name \"%s\" contains equipment")
        Mockito.`when`(projectServiceMock.getProject(PROJECT_ID)).thenReturn(project)
        Mockito.`when`(
            substations[FIRST_SUBSTATION_ID]?.let {
                substationManager.deleteSubstationIfEmpty(PROJECT_ID, FIRST_SUBSTATION_ID)
            }
        )
            .then {
                substations.remove(FIRST_SUBSTATION_ID)
                log.debug("Substation ${substations[FIRST_SUBSTATION_ID]?.name} successfully deleted")
            }

        substationManager.deleteSubstationIfEmpty(PROJECT_ID, FIRST_SUBSTATION_ID)

        Assertions.assertThat(substations.size).isEqualTo(substationAmount - 1)
    }
}
*/
