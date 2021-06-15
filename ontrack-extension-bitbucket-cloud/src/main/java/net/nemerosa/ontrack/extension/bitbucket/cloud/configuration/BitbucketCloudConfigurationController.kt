package net.nemerosa.ontrack.extension.bitbucket.cloud.configuration

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@RestController
@RequestMapping("extension/bitbucket-cloud/configurations")
class BitbucketCloudConfigurationController(
    private val configurationService: BitbucketCloudConfigurationService,
    private val securityService: SecurityService
) : AbstractResourceController() {

    /**
     * Gets the configurations
     */
    @GetMapping("")
    fun getConfigurations(): Resources<BitbucketCloudConfiguration> = Resources.of(
        configurationService.configurations,
        uri(on(javaClass).getConfigurations())
    )
        .with(Link.CREATE, uri(on(javaClass).getConfigurationForm()))
        .with(
            "_test",
            uri(on(javaClass).testConfiguration(null)),
            securityService.isGlobalFunctionGranted(
                GlobalSettings::class.java
            )
        )

    /**
     * Test for a configuration
     */
    @PostMapping("test")
    fun testConfiguration(@RequestBody configuration: BitbucketCloudConfiguration?): ConnectionResult {
        return configurationService.test(configuration)
    }

    /**
     * Gets the configuration descriptors
     */
    @GetMapping("descriptors")
    fun getConfigurationsDescriptors(): Resources<ConfigurationDescriptor> = Resources.of(
        configurationService.configurationDescriptors,
        uri(on(javaClass).getConfigurationsDescriptors())
    )

    /**
     * Form for a configuration
     */
    @GetMapping("create")
    fun getConfigurationForm(): Form = getConfigurationForm(null)

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
     * Update form
     */
    @GetMapping("{name:.*}/update")
    fun updateConfigurationForm(@PathVariable name: String): Form =
        configurationService.getConfiguration(name).let {
            getConfigurationForm(it)
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

    /**
     * Gets the form for a configuration.
     */
    private fun getConfigurationForm(config: BitbucketCloudConfiguration?): Form =
        Form.create()
            .with(
                Text.of("name")
                    .label("Name")
                    .help("Name of the configuration")
                    .value(config?.name)
                    .readOnly(config != null)
            )
            .with(
                Text.of(BitbucketCloudConfiguration::workspace.name)
                    .label("Workspace")
                    .help("Name of the Bitbucket Cloud workspace to connect to")
                    .value(config?.workspace)
            )
            .with(
                Text.of("user")
                    .label("User")
                    .help("Name of the Bitbucket Cloud user name")
                    .value(config?.user)
            )
            .with(
                Password.of("password")
                    .label("App password")
                    .help("App password to use to connect to Bitbucket Cloud")
                    .optional()
            )

}
