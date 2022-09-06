package net.nemerosa.ontrack.extension.sonarqube.measures

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SonarQubeMeasuresEventListenerTest {

    private lateinit var propertyService: PropertyService
    private lateinit var sonarQubeMeasuresCollectionService: SonarQubeMeasuresCollectionService
    private lateinit var cachedSettingsService: CachedSettingsService
    private lateinit var listener: SonarQubeMeasuresEventListener

    @BeforeEach
    fun before() {
        propertyService = mockk(relaxed = true)
        sonarQubeMeasuresCollectionService = mockk(relaxed = true)
        cachedSettingsService = mockk(relaxed = true)
        listener =
            SonarQubeMeasuresEventListener(propertyService, sonarQubeMeasuresCollectionService, cachedSettingsService)
    }

    @Test
    fun `Immediate collection when settings are enabled and property set`() {
        immediateCollectionTest(
            settingsDisabled = false,
        ) {
            // Checks that the collection of measures has been launched
            verify {
                sonarQubeMeasuresCollectionService.collect(build, sqProperty)
            }
        }
    }

    @Test
    fun `No immediate collection when settings are disabled and property set`() {
        immediateCollectionTest(
            settingsDisabled = true,
        ) {
            // Checks that the collection of measures has been launched
            verify(exactly = 0) {
                sonarQubeMeasuresCollectionService.collect(build, sqProperty)
            }
        }
    }

    private inner class ImmediateCollectionTestContext(
        settingsDisabled: Boolean,
    ) {
        val build: Build
        val event: Event
        val sqProperty: SonarQubeProperty

        init {
            // Settings
            val settings = SonarQubeMeasuresSettings(
                measures = listOf(SonarQubeMeasuresSettings.BLOCKER_VIOLATIONS),
                disabled = settingsDisabled,
                coverageThreshold = SonarQubeMeasuresSettings.DEFAULT_COVERAGE_THRESHOLD,
                blockerThreshold = SonarQubeMeasuresSettings.DEFAULT_BLOCKER_THRESHOLD,
            )
            every {
                cachedSettingsService.getCachedSettings(SonarQubeMeasuresSettings::class.java)
            } returns settings

            // Object models
            val project = Project.of(NameDescription.nd("prj", "")).withId(ID.of(1))
            val branch = Branch.of(project, NameDescription.nd("main", "")).withId(ID.of(10))
            build = Build.of(branch, NameDescription.nd("1", ""), Signature.of("test")).withId(ID.of(100))
            val vs = ValidationStamp.of(branch, NameDescription.nd("sonarqube", ""))

            // SQ config & property
            val sqConfig = SonarQubeConfiguration(
                "test",
                "https://sonarqube.nemerosa.net",
                "token"
            )
            sqProperty = SonarQubeProperty(
                configuration = sqConfig,
                key = "some:project",
                validationStamp = "sonarqube",
                measures = emptyList(),
                override = false,
                branchModel = false,
                branchPattern = "main"
            )
            every {
                propertyService.getPropertyValue(project, SonarQubePropertyType::class.java)
            } returns sqProperty

            // Always matching the branch
            every {
                sonarQubeMeasuresCollectionService.matches(any(), any())
            } returns true

            // Creating a validation event
            event = Event.of(EventFactory.NEW_VALIDATION_RUN)
                .withBuild(build)
                .withValidationStamp(vs)
                .with("status", "PASSED")
                .get()
        }

        fun runTest(test: ImmediateCollectionTestContext.() -> Unit) {
            // Receiving this event
            listener.onEvent(event)

            // Running the test
            test()
        }

    }

    private fun immediateCollectionTest(
        settingsDisabled: Boolean = false,
        test: ImmediateCollectionTestContext.() -> Unit,
    ) {
        val context = ImmediateCollectionTestContext(
            settingsDisabled = settingsDisabled,
        )
        context.runTest(test)
    }

}