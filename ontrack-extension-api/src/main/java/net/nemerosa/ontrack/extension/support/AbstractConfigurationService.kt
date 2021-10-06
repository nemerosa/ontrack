package net.nemerosa.ontrack.extension.support

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.EncryptionException
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.*
import java.util.*
import java.util.function.Function

abstract class AbstractConfigurationService<T : Configuration<T>>(
    private val configurationClass: Class<T>,
    private val configurationRepository: ConfigurationRepository,
    private val securityService: SecurityService,
    private val encryptionService: EncryptionService,
    private val eventPostService: EventPostService,
    private val eventFactory: EventFactory,
    private val ontrackConfigProperties: OntrackConfigProperties
) : ConfigurationService<T> {

    private val listeners: MutableList<ConfigurationServiceListener<T>> = LinkedList()

    /**
     * Checks the accesses by checking the [net.nemerosa.ontrack.model.security.GlobalSettings] function.
     */
    private fun checkAccess() {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
    }

    override fun getConfigurations(): List<T> {
        return configurationRepository.list(configurationClass)
            .map { config: T -> decrypt(config) }
    }

    override fun getConfigurationDescriptors(): List<ConfigurationDescriptor> {
        return securityService.asAdmin {
            configurations
                .map { it.descriptor }
        }
    }

    override fun newConfiguration(configuration: T): T {
        checkAccess()
        validateAndCheck(configuration)
        configurationRepository.save(encrypt(configuration))
        eventPostService.post(eventFactory.newConfiguration(configuration))
        listeners.forEach { listener ->
            listener.onNewConfiguration(
                configuration
            )
        }
        return configuration.obfuscate()
    }

    override fun getConfiguration(name: String): T {
        return findConfiguration(name)
            ?: throw ConfigurationNotFoundException(name)
    }

    private fun findConfiguration(name: String): T? {
        return configurationRepository
            .find(configurationClass, name)
            .getOrNull()
            ?.run { decrypt(this) }
    }

    override fun getOptionalConfiguration(name: String): Optional<T> {
        return configurationRepository.find(configurationClass, name).map { config: T -> decrypt(config) }
    }

    override fun deleteConfiguration(name: String) {
        checkAccess()
        val configuration = getConfiguration(name)
        // Notifies of the deletion BEFORE the actual deletion, giving a change to the listeners to list access the configuration
        eventPostService.post(eventFactory.deleteConfiguration(configuration))
        // Listeners
        listeners.forEach { listener ->
            listener.onDeletedConfiguration(
                configuration
            )
        }
        // Actual deletion
        configurationRepository.delete(configurationClass, name)
    }

    override fun updateConfiguration(name: String, configuration: T) {
        checkAccess()
        check(name == configuration.name) { "Configuration name must match" }
        val configToSave = injectCredentials(configuration)
        validateAndCheck(configToSave)
        configurationRepository.save(encrypt(configToSave))
        eventPostService.post(eventFactory.updateConfiguration(configuration))
        listeners.forEach { listener ->
            listener.onUpdatedConfiguration(
                configuration
            )
        }
    }

    /**
     * Adjust a configuration so that it contains a password if
     * 1) the password is empty
     * 2) the configuration already exists
     * 3) the user name is the same
     */
    private fun injectCredentials(configuration: T): T {
        val oldConfig = findConfiguration(configuration.name)
        return if (oldConfig != null) {
            configuration.injectCredentials(oldConfig)
        } else {
            configuration
        }
    }

    private fun validateAndCheck(configuration: T) {
        if (ontrackConfigProperties.configurationTest) {
            val result = validate(configuration)
            if (result.type == ConnectionResult.ConnectionResultType.ERROR) {
                throw ConfigurationValidationException(configuration, result.message)
            }
        }
    }

    override fun test(configuration: T): ConnectionResult {
        return validate(injectCredentials(configuration))
    }

    /**
     * Validates a configuration
     */
    protected abstract fun validate(configuration: T): ConnectionResult

    /**
     * Returns itself, no replacement any longer.
     */
    override fun replaceConfiguration(configuration: T, replacementFunction: Function<String, String>): T {
        return configuration
    }

    override fun getConfigurationType(): Class<T> {
        return configurationClass
    }

    protected fun encrypt(config: T): T {
        return config.encrypt {
            encryptionService.encrypt(it)
        }
    }

    override fun addConfigurationServiceListener(listener: ConfigurationServiceListener<T>) {
        listeners.add(listener)
    }

    protected fun decrypt(config: T): T {
        return try {
            config.decrypt {
                encryptionService.decrypt(it)
            }
        } catch (ex: EncryptionException) {
            config.obfuscate()
        }
    }
}