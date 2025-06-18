package net.nemerosa.ontrack.extension.tfc.ui

import net.nemerosa.ontrack.extension.support.AbstractExtensionController
import net.nemerosa.ontrack.extension.tfc.TFCExtensionFeature
import net.nemerosa.ontrack.extension.tfc.config.TFCConfiguration
import net.nemerosa.ontrack.extension.tfc.config.TFCConfigurationService
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription
import net.nemerosa.ontrack.model.support.ConnectionResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("extension/tfc")
class TFCController(
    feature: TFCExtensionFeature,
    private val configurationService: TFCConfigurationService,
) : AbstractExtensionController<TFCExtensionFeature>(feature) {

    @GetMapping("")
    override fun getDescription(): ExtensionFeatureDescription {
        @Suppress("RecursivePropertyAccessor")
        return feature.featureDescription
    }

    /**
     * Gets the configurations
     */
    @GetMapping("configurations")
    fun getConfigurations(): ResponseEntity<List<TFCConfiguration>> {
        return ResponseEntity.ok(
            configurationService.configurations,
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