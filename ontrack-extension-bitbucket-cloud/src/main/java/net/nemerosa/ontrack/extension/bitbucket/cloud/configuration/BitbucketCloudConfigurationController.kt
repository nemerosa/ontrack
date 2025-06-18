package net.nemerosa.ontrack.extension.bitbucket.cloud.configuration

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.support.ConnectionResult
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("extension/bitbucket-cloud/configurations")
class BitbucketCloudConfigurationController(
    private val configurationService: BitbucketCloudConfigurationService
) {

    /**
     * Gets the configurations
     */
    @GetMapping("")
    fun getConfigurations(): List<BitbucketCloudConfiguration> = configurationService.configurations

    /**
     * Test for a configuration
     */
    @PostMapping("test")
    fun testConfiguration(@RequestBody configuration: BitbucketCloudConfiguration?): ConnectionResult {
        return configurationService.test(configuration!!)
    }

    /**
     * Creating a configuration
     */
    @PostMapping("create")
    fun newConfiguration(@RequestBody configuration: BitbucketCloudConfiguration): BitbucketCloudConfiguration =
        configurationService.newConfiguration(configuration)

    /**
     * Gets one configuration
     */
    @GetMapping("{name:.*}")
    fun getConfiguration(@PathVariable name: String): BitbucketCloudConfiguration =
        configurationService.getConfiguration(name)

    /**
     * Deleting one configuration
     */
    @DeleteMapping("{name:.*}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteConfiguration(@PathVariable name: String): Ack {
        configurationService.deleteConfiguration(name)
        return Ack.OK
    }

    /**
     * Updating one configuration
     */
    @PutMapping("{name:.*}/update")
    fun updateConfiguration(
        @PathVariable name: String,
        @RequestBody configuration: BitbucketCloudConfiguration
    ): BitbucketCloudConfiguration {
        configurationService.updateConfiguration(name, configuration)
        return getConfiguration(name)
    }

}
