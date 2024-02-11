package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.ConfigurationProperty
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import org.springframework.stereotype.Component

/**
 * Global listener to clean properties linked to removed configurations.
 */
@Component
class ConfigurationPropertyCleanupListener(
    private val propertyService: PropertyService,
    private val structureService: StructureService,
    private val securityService: SecurityService
) : EventListener {

    override fun onEvent(event: Event) {
        if (event.eventType === EventFactory.DELETE_CONFIGURATION) {
            val configurationName = event.getValue("CONFIGURATION")
            val configurationType = event.getValue("CONFIGURATION_TYPE")
            cleanup(configurationName, configurationType)
        }
    }

    private fun cleanup(configurationName: String, configurationType: String) {
        securityService.asAdmin {
            propertyService.propertyTypes
                .filterIsInstance<ConfigurationPropertyType<*, *>>()
                .forEach { propertyType ->
                    cleanupType(propertyType, configurationName, configurationType)
                }
        }
    }

    private fun <C : UserPasswordConfiguration<C>, T : ConfigurationProperty<C>> cleanupType(
        propertyType: ConfigurationPropertyType<in C, in T>,
        configurationName: String,
        configurationType: String
    ) {
        propertyService.forEachEntityWithProperty(propertyType) { entityId, property ->
            if (property is ConfigurationProperty<*>) {
                val configuration = property.configuration
                if (configuration::class.java.name == configurationType && configuration.name == configurationName) {
                    val entity = entityId.type.getEntityFn(structureService).apply(ID.of(entityId.id))
                    propertyService.deleteProperty(entity, propertyType.typeName)
                }
            }
        }
    }

}