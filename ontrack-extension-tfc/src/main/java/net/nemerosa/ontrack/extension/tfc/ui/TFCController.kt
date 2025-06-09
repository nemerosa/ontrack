package net.nemerosa.ontrack.extension.tfc.ui

import net.nemerosa.ontrack.extension.support.AbstractExtensionController
import net.nemerosa.ontrack.extension.tfc.TFCExtensionFeature
import net.nemerosa.ontrack.extension.tfc.config.TFCConfiguration
import net.nemerosa.ontrack.extension.tfc.config.TFCConfigurationService
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@RestController
@RequestMapping("extension/tfc")
class TFCController(
    feature: TFCExtensionFeature,
    private val securityService: SecurityService,
    private val configurationService: TFCConfigurationService,
    private val structureService: StructureService,
    private val propertyService: PropertyService,
) : AbstractExtensionController<TFCExtensionFeature>(feature) {

    @GetMapping("")
    override fun getDescription(): Resource<ExtensionFeatureDescription> {
        @Suppress("RecursivePropertyAccessor")
        return Resource.of(
            feature.featureDescription,
            uri(on(javaClass).description)
        )
            .with(
                "configurations",
                uri(on(javaClass).getConfigurations()),
                securityService.isGlobalFunctionGranted(GlobalSettings::class.java)
            )
    }

    /**
     * Gets the configurations
     */
    @GetMapping("configurations")
    fun getConfigurations(): Resources<TFCConfiguration> {
        return Resources.of(
            configurationService.configurations,
            uri(on(javaClass).getConfigurations())
        )
            .with(
                "_test",
                uri(on(javaClass).testConfiguration(null)),
                securityService.isGlobalFunctionGranted(GlobalSettings::class.java)
            )
    }

    /**
     * Test for a configuration
     */
    @PostMapping("configurations/test")
    fun testConfiguration(@RequestBody configuration: TFCConfiguration?): ConnectionResult {
        return configurationService.test(configuration ?: error("Expecting a non null body"))
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
     * Creating a configuration
     */
    @PostMapping("configurations/create")
    fun newConfiguration(@RequestBody configuration: TFCConfiguration): TFCConfiguration =
        configurationService.newConfiguration(configuration)

    /**
     * Gets one configuration
     */
    @GetMapping("configurations/{name:.*}")
    fun getConfiguration(@PathVariable name: String): TFCConfiguration =
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
     * Updating one configuration
     */
    @PutMapping("configurations/{name:.*}/update")
    fun updateConfiguration(
        @PathVariable name: String,
        @RequestBody configuration: TFCConfiguration
    ): TFCConfiguration {
        configurationService.updateConfiguration(name, configuration)
        return getConfiguration(name)
    }

}