package net.nemerosa.ontrack.extension.sonarqube

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.api.support.TestBranchModelMatcherProvider
import net.nemerosa.ontrack.extension.general.BuildLinkDisplayProperty
import net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType
import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.general.validation.MetricsValidationData
import net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType
import net.nemerosa.ontrack.extension.sonarqube.client.SonarQubeClient
import net.nemerosa.ontrack.extension.sonarqube.client.SonarQubeClientFactory
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfigurationService
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasures
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasuresCollectionService
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasuresInformationExtension
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasuresSettings
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SonarQubeIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var sonarQubeConfigurationService: SonarQubeConfigurationService

    @Autowired
    private lateinit var sonarQubeMeasuresCollectionService: SonarQubeMeasuresCollectionService

    @Autowired
    private lateinit var metricsExportService: MetricsExportService

    @Autowired
    private lateinit var informationExtension: SonarQubeMeasuresInformationExtension

    @Test
    fun `Launching the collection on validation run`() {
        val returnedMeasures = mapOf(
            "measure-1" to 12.3,
            "measure-2" to 45.0
        )
        testCollectionWithListener(
            actualMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            ),
            returnedMeasures = returnedMeasures
        ) { build ->
            // Checks that metrics are exported
            returnedMeasures.forEach { (name, value) ->
                verify {
                    metricsExportService.exportMetrics(
                        metric = "ontrack_sonarqube_measure",
                        tags = mapOf(
                            "project" to build.project.name,
                            "branch" to build.branch.name,
                            "status" to "PASSED",
                            "measure" to name
                        ),
                        fields = mapOf(
                            "value" to value
                        ),
                        timestamp = any()
                    )
                }
            }
            // Checks the entity information
            val information = informationExtension.getInformation(build)
            assertNotNull(information) {
                assertIs<SonarQubeMeasures>(it.data) { q ->
                    assertEquals(
                        returnedMeasures,
                        q.measures
                    )
                }
            }
            // Checks that the metrics have been attached to the run
            val vs = structureService.findValidationStampByName(build.project.name, build.branch.name, "sonarqube")
                .getOrNull() ?: error("Cannot find the validation stamp")
            val run = structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1).firstOrNull()
                ?: error("Cannot find the validation run")
            assertNotNull(run.data, "Run has some data") { data ->
                assertEquals(
                    data.descriptor.id,
                    MetricsValidationDataType::class.java.name
                )
                assertIs<MetricsValidationData>(data.data) {
                    assertEquals(
                        returnedMeasures,
                        it.metrics
                    )
                }
            }
        }
    }

    @Test
    fun `Launching the collection on validation run with validation metrics disabled`() {
        val returnedMeasures = mapOf(
            "measure-1" to 12.3,
            "measure-2" to 45.0
        )
        testCollectionWithListener(
            actualMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            ),
            returnedMeasures = returnedMeasures,
            validationMetrics = false,
        ) { build ->
            // Checks that metrics are exported
            returnedMeasures.forEach { (name, value) ->
                verify {
                    metricsExportService.exportMetrics(
                        metric = "ontrack_sonarqube_measure",
                        tags = mapOf(
                            "project" to build.project.name,
                            "branch" to build.branch.name,
                            "status" to "PASSED",
                            "measure" to name
                        ),
                        fields = mapOf(
                            "value" to value
                        ),
                        timestamp = any()
                    )
                }
            }
            // Checks the entity information
            val information = informationExtension.getInformation(build)
            assertNotNull(information) {
                assertIs<SonarQubeMeasures>(it.data) { q ->
                    assertEquals(
                        returnedMeasures,
                        q.measures
                    )
                }
            }
            // Checks that the metrics have been attached to the run
            val vs = structureService.findValidationStampByName(build.project.name, build.branch.name, "sonarqube")
                .getOrNull() ?: error("Cannot find the validation stamp")
            val run = structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1).firstOrNull()
                ?: error("Cannot find the validation run")
            assertNull(run.data, "Not expecting any data on the run")
        }
    }

    @Test
    fun `Launching the collection on validation run without any measure`() {
        testCollectionWithListener(
            actualMeasures = null,
            returnedMeasures = null
        )
    }

    @Test
    fun `Launching the collection on validation run using a non default validation stamp`() {
        testCollectionWithListener(
            validationStamp = "sonar-qube",
            actualMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            ),
            returnedMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            )
        )
    }

    @Test
    fun `Launching the collection on validation run can be disabled at global level`() {
        testCollectionWithListener(
            globalDisabled = true,
            actualMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            ),
            returnedMeasures = null,
        )
    }

    @Test
    fun `Launching the collection on validation run with global settings`() {
        testCollectionWithListener(
            globalMeasures = listOf("measure-2"),
            actualMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            ),
            returnedMeasures = mapOf(
                "measure-2" to 45.0
            )
        )
    }

    @Test
    fun `Launching the collection on validation run with global settings completed by project`() {
        testCollectionWithListener(
            globalMeasures = listOf("measure-2"),
            projectMeasures = listOf("measure-1"),
            actualMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            ),
            returnedMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            )
        )
    }

    @Test
    fun `Launching the collection on validation run with global settings overridden by project`() {
        testCollectionWithListener(
            globalMeasures = listOf("measure-2"),
            projectMeasures = listOf("measure-1"),
            projectOverride = true,
            actualMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            ),
            returnedMeasures = mapOf(
                "measure-1" to 12.3
            )
        )
    }

    @Test
    fun `Launching the collection on validation run using build label`() {
        testCollectionWithListener(
            useLabel = true,
            buildName = "release-1.0.0",
            buildLabel = "1.0.0",
            actualMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            ),
            returnedMeasures = mapOf(
                "measure-1" to 12.3,
                "measure-2" to 45.0
            )
        )
    }

    @Test
    fun `Launching the collection on validation run with missing measures`() {
        testCollectionWithListener(
            actualMeasures = mapOf(
                "measure-1" to 12.3
            ),
            returnedMeasures = mapOf(
                "measure-1" to 12.3
            )
        )
    }

    private fun testCollectionWithListener(
        globalMeasures: List<String> = listOf("measure-1", "measure-2"),
        globalDisabled: Boolean = false,
        validationStamp: String = "sonarqube",
        validationMetrics: Boolean = true,
        projectMeasures: List<String> = emptyList(),
        projectOverride: Boolean = false,
        useLabel: Boolean = false,
        buildVersion: String = "1.0.0",
        buildName: String = "1.0.0",
        buildLabel: String? = null,
        actualMeasures: Map<String, Double>?,
        returnedMeasures: Map<String, Double>?,
        code: (Build) -> Unit = {}
    ) {
        withSonarQubeSettings(measures = globalMeasures, disabled = globalDisabled) {
            val key = uid("p")
            // Mocking the measures
            if (actualMeasures != null) {
                mockSonarQubeMeasures(
                    key,
                    buildVersion,
                    *actualMeasures.toList().toTypedArray()
                )
            }
            withConfiguredProject(
                key = key,
                stamp = validationStamp,
                measures = projectMeasures,
                override = projectOverride,
                validationMetrics = validationMetrics,
            ) {
                if (useLabel) {
                    setProperty(
                        this,
                        BuildLinkDisplayPropertyType::class.java,
                        BuildLinkDisplayProperty(true)
                    )
                }
                branch {
                    val vs = validationStamp(validationStamp)
                    build(buildName) {
                        // Label for the build
                        if (buildLabel != null) {
                            setProperty(
                                this,
                                ReleasePropertyType::class.java,
                                ReleaseProperty(buildLabel)
                            )
                        }
                        // Validates to launch the collection
                        validate(vs)
                        // Checks that some SonarQube metrics are attached to this build
                        val measures = sonarQubeMeasuresCollectionService.getMeasures(this)
                        if (returnedMeasures != null) {
                            assertNotNull(measures) {
                                assertEquals(
                                    returnedMeasures,
                                    it.measures
                                )
                            }
                        } else {
                            assertNull(measures, "No measure is expected to be returned")
                        }
                        // Additional checks
                        code(this)
                    }
                }
            }
        }
    }

    @Test
    fun `Collecting a build measures without a validation stamp`() {
        withDisabledConfigurationTest {
            withSonarQubeSettings {
                val key = uid("p")
                withConfiguredProject(key) {
                    branch {
                        build {
                            val result = sonarQubeMeasuresCollectionService.collect(
                                this,
                                getProperty(project, SonarQubePropertyType::class.java)
                            )
                            assertFalse(result.ok, "Nothing was collected")
                            assertEquals(
                                "Validation stamp sonarqube cannot be found in ${branch.entityDisplayName}",
                                result.message
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Project using all branches`() {
        withDisabledConfigurationTest {
            withSonarQubeSettings {
                val key = uid("p")
                withConfiguredProject(key) {
                    val build1 = branch<Build>("release-1.0") {
                        val vs = validationStamp("sonarqube")
                        build("1.0.0") {
                            validate(vs)
                        }
                    }
                    val build2 = branch<Build>("feature-test") {
                        val vs = validationStamp("sonarqube")
                        build("abdcefg") {
                            validate(vs)
                        }
                    }
                    // Setting the configuration of the project now, builds are still not scanned
                    mockSonarQubeMeasures(
                        key, "1.0.0",
                        "measure-1" to 1.0,
                        "measure-2" to 1.1
                    )
                    mockSonarQubeMeasures(
                        key, "abdcefg",
                        "measure-1" to 2.0,
                        "measure-2" to 2.1
                    )
                    // Scanning of the project
                    sonarQubeMeasuresCollectionService.collect(this) { println(it) }
                    // Checks the measures attached to the builds
                    assertNotNull(sonarQubeMeasuresCollectionService.getMeasures(build1)) {
                        assertEquals(
                            mapOf(
                                "measure-1" to 1.0,
                                "measure-2" to 1.1
                            ),
                            it.measures
                        )
                    }
                    assertNotNull(sonarQubeMeasuresCollectionService.getMeasures(build2)) {
                        assertEquals(
                            mapOf(
                                "measure-1" to 2.0,
                                "measure-2" to 2.1
                            ),
                            it.measures
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Project using branch pattern`() {
        withDisabledConfigurationTest {
            withSonarQubeSettings {
                val key = uid("p")
                withConfiguredProject(key, branchPattern = "release-.*") {
                    val build1 = branch<Build>("release-1.0") {
                        val vs = validationStamp("sonarqube")
                        build("1.0.0") {
                            validate(vs)
                        }
                    }
                    val build2 = branch<Build>("feature-test") {
                        val vs = validationStamp("sonarqube")
                        build("abdcefg") {
                            validate(vs)
                        }
                    }
                    // Setting the configuration of the project now, builds are still not scanned
                    mockSonarQubeMeasures(
                        key, "1.0.0",
                        "measure-1" to 1.0,
                        "measure-2" to 1.1
                    )
                    mockSonarQubeMeasures(
                        key, "abdcefg",
                        "measure-1" to 2.0,
                        "measure-2" to 2.1
                    )
                    // Scanning of the project
                    sonarQubeMeasuresCollectionService.collect(this) { println(it) }
                    // Checks the measures attached to the builds
                    assertNotNull(sonarQubeMeasuresCollectionService.getMeasures(build1)) {
                        assertEquals(
                            mapOf(
                                "measure-1" to 1.0,
                                "measure-2" to 1.1
                            ),
                            it.measures
                        )
                    }
                    assertNull(sonarQubeMeasuresCollectionService.getMeasures(build2))
                }
            }
        }
    }

    @Test
    fun `Project using branch model`() {
        withDisabledConfigurationTest {
            withSonarQubeSettings {
                val key = uid("p")
                withConfiguredProject(key, branchModel = true) {

                    // Registers the project as having a branching model
                    testBranchModelMatcherProvider.projects += name

                    val build1 = branch<Build>("release-1.0") {
                        val vs = validationStamp("sonarqube")
                        build("1.0.0") {
                            validate(vs)
                        }
                    }
                    val build2 = branch<Build>("feature-test") {
                        val vs = validationStamp("sonarqube")
                        build("abdcefg") {
                            validate(vs)
                        }
                    }
                    // Setting the configuration of the project now, builds are still not scanned
                    mockSonarQubeMeasures(
                        key, "1.0.0",
                        "measure-1" to 1.0,
                        "measure-2" to 1.1
                    )
                    mockSonarQubeMeasures(
                        key, "abdcefg",
                        "measure-1" to 2.0,
                        "measure-2" to 2.1
                    )
                    // Scanning of the project
                    sonarQubeMeasuresCollectionService.collect(this) { println(it) }
                    // Checks the measures attached to the builds
                    assertNotNull(sonarQubeMeasuresCollectionService.getMeasures(build1)) {
                        assertEquals(
                            mapOf(
                                "measure-1" to 1.0,
                                "measure-2" to 1.1
                            ),
                            it.measures
                        )
                    }
                    assertNull(sonarQubeMeasuresCollectionService.getMeasures(build2))
                }
            }
        }
    }

    @Test
    fun `Builds require a validation`() {
        withDisabledConfigurationTest {
            withSonarQubeSettings {
                val key = uid("p")
                withConfiguredProject(key) {
                    val build1 = branch<Build>("release-1.0") {
                        validationStamp("sonarqube")
                        build("1.0.0") // No validation for this one
                    }
                    val build2 = branch<Build>("feature-test") {
                        val vs = validationStamp("sonarqube")
                        build("abdcefg") {
                            validate(vs)
                        }
                    }
                    // Setting the configuration of the project now, builds are still not scanned
                    mockSonarQubeMeasures(
                        key, "1.0.0",
                        "measure-1" to 1.0,
                        "measure-2" to 1.1
                    )
                    mockSonarQubeMeasures(
                        key, "abdcefg",
                        "measure-1" to 2.0,
                        "measure-2" to 2.1
                    )
                    // Scanning of the project
                    sonarQubeMeasuresCollectionService.collect(this) { println(it) }
                    // Checks the measures attached to the builds
                    assertNull(sonarQubeMeasuresCollectionService.getMeasures(build1))
                    assertNotNull(sonarQubeMeasuresCollectionService.getMeasures(build2)) {
                        assertEquals(
                            mapOf(
                                "measure-1" to 2.0,
                                "measure-2" to 2.1
                            ),
                            it.measures
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Builds do not require a passed validation`() {
        withDisabledConfigurationTest {
            withSonarQubeSettings {
                val key = uid("p")
                withConfiguredProject(key) {
                    val build1 = branch<Build>("release-1.0") {
                        val vs = validationStamp("sonarqube")
                        build("1.0.0") {
                            validate(vs, ValidationRunStatusID.STATUS_FAILED)
                        }
                    }
                    val build2 = branch<Build>("feature-test") {
                        val vs = validationStamp("sonarqube")
                        build("abdcefg") {
                            validate(vs)
                        }
                    }
                    // Setting the configuration of the project now, builds are still not scanned
                    mockSonarQubeMeasures(
                        key, "1.0.0",
                        "measure-1" to 1.0,
                        "measure-2" to 1.1
                    )
                    mockSonarQubeMeasures(
                        key, "abdcefg",
                        "measure-1" to 2.0,
                        "measure-2" to 2.1
                    )
                    // Scanning of the project
                    sonarQubeMeasuresCollectionService.collect(this) { println(it) }
                    // Checks the measures attached to the builds
                    assertNotNull(sonarQubeMeasuresCollectionService.getMeasures(build1)) {
                        assertEquals(
                            mapOf(
                                "measure-1" to 1.0,
                                "measure-2" to 1.1
                            ),
                            it.measures
                        )
                    }
                    assertNotNull(sonarQubeMeasuresCollectionService.getMeasures(build2)) {
                        assertEquals(
                            mapOf(
                                "measure-1" to 2.0,
                                "measure-2" to 2.1
                            ),
                            it.measures
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Branches require a validation`() {
        withDisabledConfigurationTest {
            withSonarQubeSettings {
                val key = uid("p")
                withConfiguredProject(key) {
                    val build1 = branch<Build>("release-1.0") {
                        build("1.0.0") // No validation for this one
                    }
                    val build2 = branch<Build>("feature-test") {
                        val vs = validationStamp("sonarqube")
                        build("abdcefg") {
                            validate(vs)
                        }
                    }
                    // Setting the configuration of the project now, builds are still not scanned
                    mockSonarQubeMeasures(
                        key, "1.0.0",
                        "measure-1" to 1.0,
                        "measure-2" to 1.1
                    )
                    mockSonarQubeMeasures(
                        key, "abdcefg",
                        "measure-1" to 2.0,
                        "measure-2" to 2.1
                    )
                    // Scanning of the project
                    sonarQubeMeasuresCollectionService.collect(this) { println(it) }
                    // Checks the measures attached to the builds
                    assertNull(sonarQubeMeasuresCollectionService.getMeasures(build1))
                    assertNotNull(sonarQubeMeasuresCollectionService.getMeasures(build2)) {
                        assertEquals(
                            mapOf(
                                "measure-1" to 2.0,
                                "measure-2" to 2.1
                            ),
                            it.measures
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Adding a new SonarQube configuration`() {
        withDisabledConfigurationTest {
            val name = uid("S")
            asUserWith<GlobalSettings> {
                val saved = sonarQubeConfigurationService.newConfiguration(
                    SonarQubeConfiguration(
                        name,
                        "https://sonarqube.nemerosa.net",
                        "my-ultra-secret-token"
                    )
                )
                assertEquals(name, saved.name)
                assertEquals("https://sonarqube.nemerosa.net", saved.url)
                assertEquals("", saved.password)
                // Gets the list of configurations
                val configs = sonarQubeConfigurationService.configurations
                // Checks we find the one we just created
                assertNotNull(
                    configs.find {
                        it.name == name
                    }
                )
                // Getting it by name
                val found = sonarQubeConfigurationService.getConfiguration(name)
                assertEquals(name, found.name)
                assertEquals("https://sonarqube.nemerosa.net", found.url)
                assertEquals("my-ultra-secret-token", found.password)
            }
        }
    }

    @Test
    fun `Project SonarQube property`() {
        withDisabledConfigurationTest {
            // Creates a configuration
            val name = uid("S")
            val configuration = createSonarQubeConfiguration(name)
            project {
                // Property
                setProperty(
                    this, SonarQubePropertyType::class.java,
                    SonarQubeProperty(
                        configuration,
                        "my:key",
                        "sonarqube",
                        listOf("measure-1"),
                        false,
                        branchModel = true,
                        branchPattern = "master|develop",
                        validationMetrics = true,
                    )
                )
                // Gets the property back
                val property: SonarQubeProperty? = getProperty(this, SonarQubePropertyType::class.java)
                assertNotNull(property) {
                    assertEquals(name, it.configuration.name)
                    assertEquals("https://sonarqube.nemerosa.net", it.configuration.url)
                    assertEquals("my-ultra-secret-token", it.configuration.password)
                    assertEquals("my:key", it.key)
                    assertEquals("https://sonarqube.nemerosa.net/dashboard?id=my%3Akey", it.projectUrl)
                    assertEquals(listOf("measure-1"), it.measures)
                    assertEquals(false, it.override)
                    assertEquals(true, it.branchModel)
                    assertEquals("master|develop", it.branchPattern)
                }
            }
        }
    }

    @Test
    fun `Project property deleted when configuration is deleted`() {
        withDisabledConfigurationTest {
            val configuration = createSonarQubeConfiguration()
            project {
                // Sets the property
                setSonarQubeProperty(configuration, "my:key")
                // Deleting the configuration
                asAdmin {
                    sonarQubeConfigurationService.deleteConfiguration(configuration.name)
                }
                // Gets the property of the project
                val property: SonarQubeProperty? = getProperty(this, SonarQubePropertyType::class.java)
                assertNull(property, "Project property has been removed")
            }
        }
    }

    /**
     * Creating a new configuration
     */
    private fun createSonarQubeConfiguration(name: String = uid("S")) =
        asUserWith<GlobalSettings, SonarQubeConfiguration> {
            sonarQubeConfigurationService.newConfiguration(
                SonarQubeConfiguration(
                    name,
                    "https://sonarqube.nemerosa.net",
                    "my-ultra-secret-token"
                )
            )
        }

    /**
     * Sets a project property
     */
    private fun Project.setSonarQubeProperty(
        configuration: SonarQubeConfiguration,
        key: String,
        stamp: String = "sonarqube",
        measures: List<String> = emptyList(),
        override: Boolean = false,
        branchModel: Boolean = false,
        branchPattern: String? = null,
        validationMetrics: Boolean = true,
    ) {
        setProperty(
            this, SonarQubePropertyType::class.java,
            SonarQubeProperty(
                configuration = configuration,
                key = key,
                validationStamp = stamp,
                measures = measures,
                override = override,
                branchModel = branchModel,
                branchPattern = branchPattern,
                validationMetrics = validationMetrics,
            )
        )
    }

    /**
     * Testing with some SonarQube measures
     */
    private fun withSonarQubeSettings(
        measures: List<String> = listOf("measure-1", "measure-2"),
        disabled: Boolean = false,
        code: () -> Unit
    ) {
        val settings = settingsService.getCachedSettings(SonarQubeMeasuresSettings::class.java)
        try {
            // Sets the settings
            asAdmin {
                settingsManagerService.saveSettings(
                    SonarQubeMeasuresSettings(
                        measures = measures,
                        disabled = disabled,
                        coverageThreshold = 80,
                        blockerThreshold = 5
                    )
                )
            }
            // Runs the code
            code()
        } finally {
            // Restores the initial settings (only in case of success)
            asAdmin {
                settingsManagerService.saveSettings(settings)
            }
        }
    }

    /**
     * Testing with a project configured for SonarQube
     */
    private fun withConfiguredProject(
        key: String,
        stamp: String = "sonarqube",
        measures: List<String> = emptyList(),
        override: Boolean = false,
        branchModel: Boolean = false,
        branchPattern: String? = null,
        validationMetrics: Boolean = true,
        code: Project.() -> Unit
    ) {
        withDisabledConfigurationTest {
            val config = createSonarQubeConfiguration()
            project {
                setSonarQubeProperty(
                    configuration = config,
                    key = key,
                    stamp = stamp,
                    measures = measures,
                    override = override,
                    branchModel = branchModel,
                    branchPattern = branchPattern,
                    validationMetrics = validationMetrics,
                )
                code()
            }
        }
    }

    private fun mockSonarQubeMeasures(key: String, version: String, vararg measures: Pair<String, Double>) {
        every {
            client.getMeasuresForVersion(
                key = key,
                branch = any(),
                version = version,
                metrics = any()
            )
        } answers { invocation ->
            // List of desired measures
            @Suppress("UNCHECKED_CAST")
            val measureList: List<String> = invocation.invocation.args[3] as List<String>
            // Map of measures
            val index = measures.toMap()
            // Results
            val result = mutableMapOf<String, Double?>()
            // Collect all results
            measureList.forEach { measure ->
                result[measure] = index[measure]
            }
            // OK
            result
        }
    }

    @Autowired
    private lateinit var client: SonarQubeClient

    @Autowired
    private lateinit var testBranchModelMatcherProvider: TestBranchModelMatcherProvider

    @Configuration
    @Profile(RunProfile.UNIT_TEST)
    class SonarQubeITConfiguration {

        /**
         * Export of metrics
         */
        @Bean
        @Primary
        fun metricsExportService() = mockk<MetricsExportService>(relaxed = true)

        /**
         * Client mock
         */
        @Bean
        fun sonarQubeClient() = mockk<SonarQubeClient>(relaxed = true)

        /**
         * Factory
         */
        @Bean
        @Primary
        fun sonarQubeClientFactory(client: SonarQubeClient) = object : SonarQubeClientFactory {
            override fun getClient(configuration: SonarQubeConfiguration): SonarQubeClient {
                return client
            }
        }

        /**
         * Model matcher
         */
        @Bean
        fun testBranchModelMatcherProvider() = TestBranchModelMatcherProvider()

    }

}