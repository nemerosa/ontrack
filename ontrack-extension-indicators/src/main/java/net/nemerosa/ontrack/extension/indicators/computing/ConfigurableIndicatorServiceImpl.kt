package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ConfigurableIndicatorServiceImpl(
    private val storageService: StorageService,
    private val extensionManager: ExtensionManager,
) : ConfigurableIndicatorService {

    override fun getConfigurableIndicatorType(id: String): ConfigurableIndicatorType<*, *> =
        extensionManager.getExtensions(IndicatorComputer::class.java)
            .flatMap { it.configurableIndicators }
            .find { it.id == id }
            ?: throw ConfigurableIndicatorTypeIdNotFoundException(id)

    override fun getConfigurableIndicatorStates(): List<ConfigurableIndicatorTypeState<*, *>> {
        // Gets the list of all indicators from all the computers
        val indicators = extensionManager.getExtensions(IndicatorComputer::class.java)
            .flatMap { computer -> computer.configurableIndicators }
        // Loads all types & their state
        return indicators.map { type ->
            ConfigurableIndicatorTypeState(
                type = type,
                state = getConfigurableIndicatorState(type)
            )
        }
    }

    override fun saveConfigurableIndicator(type: ConfigurableIndicatorType<*, *>, state: ConfigurableIndicatorState?) {
        if (state != null) {
            storageService.store(
                ConfigurableIndicatorState::class.java.name,
                type.id,
                state.toStore()
            )
        } else {
            storageService.delete(
                ConfigurableIndicatorState::class.java.name,
                type.id
            )
        }
    }

    override fun getConfigurableIndicatorState(type: ConfigurableIndicatorType<*, *>): ConfigurableIndicatorState? =
        storageService.retrieve(
            ConfigurableIndicatorState::class.java.name,
            type.id,
            StoredConfigurableIndicatorState::class.java
        ).getOrNull()?.fromStore(type)

    private class StoredConfigurableIndicatorState(
        val enabled: Boolean,
        val link: String?,
        val values: Map<String, String?>
    ) {
        fun fromStore(type: ConfigurableIndicatorType<*, *>): ConfigurableIndicatorState =
            ConfigurableIndicatorState(
                enabled = enabled,
                link = link,
                values = ConfigurableIndicatorState.toAttributeList(type, values),
            )
    }

    private fun ConfigurableIndicatorState.toStore() = StoredConfigurableIndicatorState(
        enabled = enabled,
        link = link,
        values = values.associate { it.attribute.key to it.value }
    )

}
