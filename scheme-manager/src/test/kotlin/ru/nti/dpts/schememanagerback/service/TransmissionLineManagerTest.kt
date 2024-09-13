/*
package ru.nti.dpts.schememanagerback.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.scheme.controller.v2.TransmissionLineCreateDto
import ru.nti.dpts.schememanagerback.manager.TransmissionLineManager
import ru.nti.dpts.schememanagerback.mapper.mapToView
import ru.nti.dpts.schememanagerback.scheme.persister.ProjectRepository
import java.util.*

private const val PROJECT_ID = "16df1718-1611-435b-b00c-682daee0ddfb"
private const val TRANSMISSION_LINE_NAME = "test_name"

@DataMongoTest
@Import(value = [TestConfigurationToServices::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransmissionLineManagerTest {

    @Autowired
    lateinit var transmissionLineManager: TransmissionLineManager

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var projectService: ProjectService

    private val scheme = SchemeBuilder().build()

    @BeforeAll
    fun createProject() {
        projectService.saveProject(PROJECT_ID, "", Date())
        projectService.updateProject(
            PROJECT_ID,
            scheme.mapToView(),
            false
        )
    }

    @Test
    fun `create transmission line`() {
        val transmissionLine = transmissionLineManager.createTransmissionLine(
            TransmissionLineCreateDto(
                PROJECT_ID,
                TRANSMISSION_LINE_NAME
            )
        )
        val transmissionLines =
            transmissionLineManager.getAllTransmissionLines(PROJECT_ID)

        Assertions.assertThat(transmissionLines.contains(transmissionLine))
        Assertions.assertThat(transmissionLines.size).isEqualTo(1)

        transmissionLineManager.deleteTransmissionLine(
            PROJECT_ID,
            transmissionLine.id
        )
    }

    @Test
    fun `create twins transmission line`() {
        val transmissionLine = transmissionLineManager.createTransmissionLine(
            TransmissionLineCreateDto(
                PROJECT_ID,
                TRANSMISSION_LINE_NAME
            )
        )

        Assertions.assertThatIllegalArgumentException()
            .isThrownBy {
                transmissionLineManager.createTransmissionLine(
                    TransmissionLineCreateDto(
                        PROJECT_ID,
                        TRANSMISSION_LINE_NAME
                    )
                )
            }
            .withMessage("ЛЭП с наименованием \"$TRANSMISSION_LINE_NAME\" уже существует")

        transmissionLineManager.deleteTransmissionLine(
            PROJECT_ID,
            transmissionLine.id
        )
    }

    @Test
    fun `get all transmission lines`() {
        val transmissionLine = transmissionLineManager.createTransmissionLine(
            TransmissionLineCreateDto(
                PROJECT_ID,
                TRANSMISSION_LINE_NAME
            )
        )
        val transmissionLines =
            transmissionLineManager.getAllTransmissionLines(PROJECT_ID)

        Assertions.assertThat(transmissionLines.size).isEqualTo(1)

        transmissionLineManager.deleteTransmissionLine(
            PROJECT_ID,
            transmissionLine.id
        )
    }

    @Test
    fun `delete transmission line`() {
        val transmissionLine = transmissionLineManager.createTransmissionLine(
            TransmissionLineCreateDto(
                PROJECT_ID,
                TRANSMISSION_LINE_NAME
            )
        )
        transmissionLineManager.deleteTransmissionLine(
            PROJECT_ID,
            transmissionLine.id
        )

        val transmissionLines =
            transmissionLineManager.getAllTransmissionLines(PROJECT_ID)
        Assertions.assertThat(transmissionLines.size).isEqualTo(0)
    }

    @AfterAll
    fun deleteProject() {
        projectRepository.deleteById(PROJECT_ID)
    }
}
*/
