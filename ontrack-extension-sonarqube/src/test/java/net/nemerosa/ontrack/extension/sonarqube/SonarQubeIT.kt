package net.nemerosa.ontrack.extension.sonarqube

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.general.BuildLinkDisplayProperty
import net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType
import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
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
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import kotlin.test.assertEquals
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
                verify(metricsExportService).exportMetrics(
                        metric = eq("ontrack_sonarqube_measure"),
                        tags = eq(mapOf(
                                "project" to build.project.name,
                                "branch" to build.branch.name,
                                "build" to build.name,
                                "version" to "1.0.0",
                                "metric" to name
                        )),
                        fields = eq(mapOf(
                                "value" to value
                        )),
                        timestamp = any()
                )
            }
            // Checks the entity information
            val information = informationExtension.getInformation(build).getOrNull()
            assertNotNull(information) {
                assertIs<SonarQubeMeasures>(it.data) { q ->
                    assertEquals(
                            returnedMeasures,
                            q.measures
                    )
                }
            }
        }
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
            validationStamp: String = "sonarqube",
            useLabel: Boolean = false,
            buildVersion: String = "1.0.0",
            buildName: String = "1.0.0",
            buildLabel: String? = null,
            actualMeasures: Map<String, Double>,
            returnedMeasures: Map<String, Double>,
            code: (Build) -> Unit = {}
    ) {
        withSonarQubeSettings {
            val key = uid("p")
            // Mocking the measures
            mockSonarQubeMeasures(
                    key,
                    buildVersion,
                    *actualMeasures.toList().toTypedArray()
            )
            withConfiguredProject(key = key, stamp = validationStamp) {
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
                        assertNotNull(measures) {
                            assertEquals(
                                    returnedMeasures,
                                    it.measures
                            )
                        }
                        // Additional checks
                        code(this)
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
                setProperty(this, SonarQubePropertyType::class.java,
                        SonarQubeProperty(
                                configuration,
                                "my:key",
                                "sonarqube",
                                listOf("measure-1"),
                                false,
                                branchModel = true,
                                branchPattern = "master|develop"
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
    private fun Project.setSonarQubeProperty(configuration: SonarQubeConfiguration, key: String, stamp: String = "sonarqube") {
        setProperty(this, SonarQubePropertyType::class.java,
                SonarQubeProperty(
                        configuration = configuration,
                        key = key,
                        validationStamp = stamp,
                        measures = emptyList(),
                        override = false,
                        branchModel = false,
                        branchPattern = null
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
                        SonarQubeMeasuresSettings(measures, disabled)
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
    private fun withConfiguredProject(key: String, stamp: String = "sonarqube", code: Project.() -> Unit) {
        withDisabledConfigurationTest {
            val config = createSonarQubeConfiguration()
            project {
                setSonarQubeProperty(config, key, stamp)
                code()
            }
        }
    }

    private fun mockSonarQubeMeasures(key: String, version: String, vararg measures: Pair<String, Double>) {
        whenever(client.getMeasuresForVersion(
                eq(key),
                eq(version),
                any()
        )).then { invocation ->
            // List of desired measures
            @Suppress("UNCHECKED_CAST")
            val measureList: List<String> = invocation.arguments[2] as List<String>
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

    @Configuration
    @Profile(RunProfile.UNIT_TEST)
    class SonarQubeITConfiguration {

        /**
         * Export of metrics
         */
        @Bean
        @Primary
        fun metricsExportService() = mock<MetricsExportService>()

        /**
         * Client mock
         */
        @Bean
        fun sonarQubeClient() = mock<SonarQubeClient>()

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

    }

}