package net.nemerosa.ontrack.extension.sonarqube.measures

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType
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
    private lateinit var validationRunService: ValidationRunService
    private lateinit var metricsValidationDataType: MetricsValidationDataType

    @BeforeEach
    fun before() {
        propertyService = mockk(relaxed = true)
        sonarQubeMeasuresCollectionService = mockk(relaxed = true)
        cachedSettingsService = mockk(relaxed = true)
        validationRunService = mockk(relaxed = true)
        metricsValidationDataType = mockk(relaxed = true)
        listener =
            SonarQubeMeasuresEventListener(
                propertyService,
                sonarQubeMeasuresCollectionService,
                cachedSettingsService,
                validationRunService,
                metricsValidationDataType,
                mockk(),
            )
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
        val validationMetrics: Boolean = true,
    ) {
        val build: Build
        val run: ValidationRun
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
            val vs = ValidationStamp.of(branch, NameDescription.nd("sonarqube", "")).withId(ID.of(100))
            run = ValidationRun.of(build, vs, 1, Signature.of("test"), ValidationRunStatusID.STATUS_PASSED, null)
                .withId(ID.of(1000))

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
                branchPattern = "main",
                validationMetrics = true,
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
                .withValidationRun(run)
                .with("status", run.lastStatusId)
                .build()
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
        validationMetrics: Boolean = true,
        test: ImmediateCollectionTestContext.() -> Unit,
    ) {
        val context = ImmediateCollectionTestContext(
            settingsDisabled = settingsDisabled,
            validationMetrics = validationMetrics,
        )
        context.runTest(test)
    }

}