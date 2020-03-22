package net.nemerosa.ontrack.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.exceptions.PropertyTypeNotFoundException
import net.nemerosa.ontrack.model.exceptions.PropertyUnsupportedEntityTypeException
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.repository.PropertyRepository
import net.nemerosa.ontrack.repository.TProperty
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
import kotlin.reflect.KClass

@Service
@Transactional
class PropertyServiceImpl(
        private val eventPostService: EventPostService,
        private val eventFactory: EventFactory,
        private val propertyRepository: PropertyRepository,
        private val securityService: SecurityService,
        private val extensionManager: ExtensionManager
) : PropertyService {

    override val propertyTypes: List<PropertyType<*>> by lazy {
        val types = extensionManager.getExtensions(PropertyType::class.java)
        val result = mutableListOf<PropertyType<*>>()
        types.forEach { result.add(it) }
        result
    }

    /**
     * The number of available property types is fairly limited so a static cache is enough.
     */
    private val cache: Map<String, PropertyType<*>?> by lazy {
        propertyTypes.associateBy { it.typeName }
    }

    override fun <T> getPropertyTypeByName(propertyTypeName: String): PropertyType<T> {
        @Suppress("UNCHECKED_CAST")
        return cache[propertyTypeName] as PropertyType<T>? ?: throw PropertyTypeNotFoundException(propertyTypeName)
    }

    override fun getProperties(entity: ProjectEntity): List<Property<*>> {
        // With all the existing properties...
        return propertyTypes
                // ... filters them by entity
                .filter { type -> type.supportedEntityTypes.contains(entity.projectEntityType) }
                // ... filters them by access right
                .filter { type -> type.canView(entity, securityService) }
                // ... loads them from the store
                .map { type -> getProperty(type, entity) }
                // .. flags with edition rights
                .map { prop -> prop.editable(prop.type.canEdit(entity, securityService)) }
    }

    override fun <T> getProperty(entity: ProjectEntity, propertyTypeName: String): Property<T> {
        // Gets the property using its fully qualified type name
        val propertyType: PropertyType<T> = getPropertyTypeByName(propertyTypeName)
        // Access
        return getProperty(propertyType, entity)
    }

    override fun <T> getProperty(entity: ProjectEntity, propertyTypeClass: Class<out PropertyType<T>>): Property<T> {
        return getProperty(entity, propertyTypeClass.name)
    }

    override fun <T> hasProperty(entity: ProjectEntity, propertyTypeClass: Class<out PropertyType<T>>): Boolean {
        return propertyRepository.hasProperty(propertyTypeClass.javaClass.name, entity.projectEntityType, entity.id)
    }

    override fun editProperty(entity: ProjectEntity, propertyTypeName: String, data: JsonNode): Ack {
        // Gets the property using its fully qualified type name
        val propertyType: PropertyType<*> = getPropertyTypeByName<Any>(propertyTypeName)
        // Edits the property
        return editProperty(entity, propertyType, data)
    }

    override fun deleteProperty(entity: ProjectEntity, propertyTypeName: String): Ack {
        // Gets the property using its fully qualified type name
        val propertyType: PropertyType<*> = getPropertyTypeByName<Any>(propertyTypeName)
        // Deletes the property
        return deleteProperty(entity, propertyType)
    }

    private fun <T> deleteProperty(entity: ProjectEntity, propertyType: PropertyType<T>): Ack {
        // Checks for edition
        if (!propertyType.canEdit(entity, securityService)) {
            throw AccessDeniedException("Property is not opened for viewing.")
        }
        // Gets the existing value
        val value = getPropertyValue(propertyType, entity)
        // If existing, deletes it
        return if (value != null) {
            val ack = propertyRepository.deleteProperty(propertyType.javaClass.name, entity.projectEntityType, entity.id)
            if (ack.isSuccess) {
                // Property deletion event
                eventPostService.post(eventFactory.propertyDelete(entity, propertyType))
                // Listener
                propertyType.onPropertyDeleted(entity, value)
            }
            // OK
            ack
        } else {
            Ack.NOK
        }
    }

    override fun <T> editProperty(entity: ProjectEntity, propertyType: Class<out PropertyType<T>>, data: T): Ack {
        // Gets the property type by name
        val actualPropertyType: PropertyType<T> = getPropertyTypeByName(propertyType.name)
        // Actual edition
        return editProperty(entity, actualPropertyType, data)
    }

    private fun <T> editProperty(entity: ProjectEntity, propertyType: PropertyType<T>, data: JsonNode): Ack {
        // Gets the value and validates it
        val value = propertyType.fromClient(data)
        // Actual edition
        return editProperty(entity, propertyType, value)
    }

    private fun <T> editProperty(entity: ProjectEntity, propertyType: PropertyType<T>, value: T): Ack {
        // Checks for edition
        if (!propertyType.canEdit(entity, securityService)) {
            throw AccessDeniedException("Property is not opened for edition.")
        }
        // Gets the JSON for the storage
        val storage = propertyType.forStorage(value)
        // Stores the property
        propertyRepository.saveProperty(
                propertyType.javaClass.name,
                entity.projectEntityType,
                entity.id,
                storage
        )
        // Property change event
        eventPostService.post(eventFactory.propertyChange(entity, propertyType))
        // Listener
        propertyType.onPropertyChanged(entity, value)
        // OK
        return Ack.OK
    }

    protected fun <T> getProperty(type: PropertyType<T>?, entity: ProjectEntity): Property<T> {
        val value = getPropertyValue(type, entity)
        return if (value != null) Property.of(type, value) else Property.empty(type)
    }

    protected fun <T> getPropertyValue(type: PropertyType<T>?, entity: ProjectEntity): T? {
        // Supported entity?
        if (!type!!.supportedEntityTypes.contains(entity.projectEntityType)) {
            throw PropertyUnsupportedEntityTypeException(type.javaClass.name, entity.projectEntityType)
        }
        // Checks for viewing
        if (!type.canView(entity, securityService)) {
            throw AccessDeniedException("Property is not opened for viewing.")
        }
        // Gets the raw information from the repository
        val t = propertyRepository.loadProperty(
                type.javaClass.name,
                entity.projectEntityType,
                entity.id)
                ?: return null
        // If null, returns null
        // Converts the stored value into an actual value
        return type.fromStorage(t.json)
    }

    override fun getPropertyEditionForm(entity: ProjectEntity, propertyTypeName: String): Form {
        // Gets the property using its fully qualified type name
        val propertyType: PropertyType<*> = getPropertyTypeByName<Any>(propertyTypeName)
        // Gets the edition form for this type
        return getPropertyEditionForm(entity, propertyType)
    }

    override fun <T> searchWithPropertyValue(
            propertyTypeClass: Class<out PropertyType<T>>,
            entityLoader: BiFunction<ProjectEntityType, ID, ProjectEntity>,
            predicate: Predicate<T>): Collection<ProjectEntity> {
        // Gets the property type
        val propertyTypeName = propertyTypeClass.name
        val propertyType: PropertyType<T> = getPropertyTypeByName(propertyTypeName)
        // Search
        return propertyRepository.searchByProperty(
                propertyTypeName,
                entityLoader,
                Predicate { t: TProperty -> predicate.test(propertyType.fromStorage(t.json)) }
        )
    }

    override fun <T> forEachEntityWithProperty(propertyTypeClass: KClass<out PropertyType<T>>, consumer: (ProjectEntityID, T) -> Unit) {
        // Gets the property type name
        val propertyTypeName = propertyTypeClass.java.name
        // Gets the property type by name
        val actualPropertyType: PropertyType<T> = getPropertyTypeByName(propertyTypeName)
        // Loops over the properties
        propertyRepository.forEachEntityWithProperty(
                propertyTypeName
        ) { t: TProperty ->
            consumer(
                    ProjectEntityID(t.entityType, t.entityId.value),
                    actualPropertyType.fromStorage(t.json)
            )
        }
    }

    override fun <T> findBuildByBranchAndSearchkey(branchId: ID, propertyType: Class<out PropertyType<T>>, searchKey: String): ID? {
        // Gets the property type by name
        val actualPropertyType: PropertyType<T> = getPropertyTypeByName(propertyType.name)
        // Gets the search arguments
        val searchArguments = actualPropertyType.getSearchArguments(searchKey)
        return if (searchArguments != null) {
            propertyRepository.findBuildByBranchAndSearchkey(branchId, propertyType.name, searchArguments)
        } else {
            null
        }
    }

    override fun <T> findByEntityTypeAndSearchkey(entityType: ProjectEntityType, propertyType: Class<out PropertyType<T>>, searchKey: String): List<ID> {
        // Gets the property type by name
        val actualPropertyType: PropertyType<T> = getPropertyTypeByName(propertyType.name)
        // Gets the search arguments
        val searchArguments = actualPropertyType.getSearchArguments(searchKey)
        return if (searchArguments != null) {
            propertyRepository.findByEntityTypeAndSearchkey(entityType, propertyType.name, searchArguments)
        } else {
            emptyList()
        }
    }

    override fun <T> copyProperty(sourceEntity: ProjectEntity, property: Property<T>, targetEntity: ProjectEntity, replacementFn: Function<String, String>) {
        // Property copy
        val data = property.type.copy(sourceEntity, property.value, targetEntity, replacementFn)
        // Direct edition
        editProperty(targetEntity, property.type, data)
    }

    protected fun <T> getPropertyEditionForm(entity: ProjectEntity, propertyType: PropertyType<T>): Form {
        // Checks for edition
        if (!propertyType.canEdit(entity, securityService)) {
            throw AccessDeniedException("Property is not opened for edition.")
        }
        // Gets the value for this property
        val value: T? = getPropertyValue(propertyType, entity)
        // Gets the form
        return propertyType.getEditionForm(entity, value)
    }

}