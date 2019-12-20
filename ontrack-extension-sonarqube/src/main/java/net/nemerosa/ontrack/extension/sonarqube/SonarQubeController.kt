package net.nemerosa.ontrack.extension.sonarqube

import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfigurationService
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasuresCollectionResult
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasuresCollectionService
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType
import net.nemerosa.ontrack.extension.support.AbstractExtensionController
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@RestController
@RequestMapping("extension/sonarqube")
class SonarQubeController(
        feature: SonarQubeExtensionFeature,
        private val securityService: SecurityService,
        private val configurationService: SonarQubeConfigurationService,
        private val structureService: StructureService,
        private val propertyService: PropertyService,
        private val sonarQubeMeasuresCollectionService: SonarQubeMeasuresCollectionService
) : AbstractExtensionController<SonarQubeExtensionFeature>(feature) {

    @GetMapping("")
    override fun getDescription(): Resource<ExtensionFeatureDescription> {
        @Suppress("RecursivePropertyAccessor")
        return Resource.of(
                feature.featureDescription,
                uri(on(javaClass).description)
        )
                .with("configurations", uri(on(javaClass).getConfigurations()), securityService.isGlobalFunctionGranted(GlobalSettings::class.java))
    }

    /**
     * Collecting measures for a build
     */
    @PutMapping("/build/{buildId}/measures")
    fun collectBuildMeasures(@PathVariable buildId: ID): SonarQubeMeasuresCollectionResult {
        val build = structureService.getBuild(buildId)
        val property: SonarQubeProperty? = propertyService.getProperty(build.project, SonarQubePropertyType::class.java).value
        return if (property != null && securityService.isProjectFunctionGranted(build, ProjectEdit::class.java)) {
            return if (sonarQubeMeasuresCollectionService.matches(build, property)) {
                sonarQubeMeasuresCollectionService.collect(build, property)
            } else {
                SonarQubeMeasuresCollectionResult.error("${build.entityDisplayName} is not eligible for SonarQube collection")
            }
        } else {
            SonarQubeMeasuresCollectionResult.error("SonarQube collection is not accessible for this project or your security profile does not grant you the right to request a scan.")
        }
    }

    /**
     * Gets the configurations
     */
    @GetMapping("configurations")
    fun getConfigurations(): Resources<SonarQubeConfiguration> {
        return Resources.of<SonarQubeConfiguration>(
                configurationService.configurations,
                uri(on(javaClass).getConfigurations())
        )
                .with(Link.CREATE, uri(on(javaClass).getConfigurationForm()))
                .with("_test", uri(on(javaClass).testConfiguration(null)), securityService.isGlobalFunctionGranted(GlobalSettings::class.java))
    }

    /**
     * Test for a configuration
     */
    @PostMapping("configurations/test")
    fun testConfiguration(@RequestBody configuration: SonarQubeConfiguration?): ConnectionResult {
        return configurationService.test(configuration)
    }

    /**
     * Gets the configuration descriptors
     */
    @GetMapping("configurations/descriptors")
    fun getConfigurationsDescriptors(): Resources<ConfigurationDescriptor> {
        return Resources.of(
                configurationService.configurationDescriptors,
                uri(on(javaClass).getConfigurationsDescriptors())
        )
    }

    /**
     * Form for a configuration
     */
    @GetMapping("configurations/create")
    fun getConfigurationForm(): Form {
        return Form.create()
                .with(Form.defaultNameField())
                .with(
                        Text.of("url")
                                .label("URL")
                                .help("URL to the SonarQube instance."))
                .with(
                        Password.of("password")
                                .label("Token")
                                .length(80)
                )
    }

    /**
     * Creating a configuration
     */
    @PostMapping("configurations/create")
    fun newConfiguration(@RequestBody configuration: SonarQubeConfiguration): SonarQubeConfiguration =
            configurationService.newConfiguration(configuration)

    /**
     * Gets one configuration
     */
    @GetMapping("configurations/{name:.*}")
    fun getConfiguration(@PathVariable name: String): SonarQubeConfiguration =
            configurationService.getConfiguration(name)

    /**
     * Deleting one configuration
     */
    @DeleteMapping("configurations/{name:.*}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteConfiguration(@PathVariable name: String): Ack {
        configurationService.deleteConfiguration(name)
        return Ack.OK
    }

    /**
     * Update form
     */
    @GetMapping("configurations/{name:.*}/update")
    fun updateConfigurationForm(@PathVariable name: String): Form {
        return configurationService.getConfiguration(name).run {
            getConfigurationForm()
                    .with(Form.defaultNameField().readOnly().value(name))
                    .fill("url", url)
                    .fill("password", "")
        }
    }

    /**
     * Updating one configuration
     */
    @PutMapping("configurations/{name:.*}/update")
    fun updateConfiguration(@PathVariable name: String, @RequestBody configuration: SonarQubeConfiguration): SonarQubeConfiguration {
        configurationService.updateConfiguration(name, configuration)
        return getConfiguration(name)
    }

}