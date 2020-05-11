package net.nemerosa.ontrack.extension.indicators.model

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class IndicatorTypeServiceImpl(
        private val indicatorCategoryService: IndicatorCategoryService,
        private val indicatorValueTypeService: IndicatorValueTypeService,
        private val storageService: StorageService,
        private val securityService: SecurityService
) : IndicatorTypeService {

    override fun findAll(): List<IndicatorType<*, *>> {
        return storageService.getKeys(STORE).mapNotNull { key ->
            storageService.retrieve(STORE, key, StoredIndicatorType::class.java).getOrNull()
        }.mapNotNull {
            fromStorage<Any, Any>(it)
        }.sortedWith(
                compareBy(
                        { it.category.name },
                        { it.shortName }
                )
        )
    }

    override fun findTypeById(typeId: String): IndicatorType<*, *>? =
            storageService.retrieve(STORE, typeId, StoredIndicatorType::class.java)
                    .getOrNull()
                    ?.let { fromStorage<Any, Any>(it) }

    override fun getTypeById(typeId: String): IndicatorType<*, *> =
            findTypeById(typeId) ?: throw IndicatorTypeNotFoundException(typeId)

    override fun findByCategory(category: IndicatorCategory): List<IndicatorType<*, *>> {
        return findAll().filter {
            it.category.id == category.id
        }.sortedWith(
                compareBy(
                        { it.category.name },
                        { it.shortName }
                )
        )
    }

    private fun <T, C> fromStorage(stored: StoredIndicatorType): IndicatorType<T, C>? {
        val category = indicatorCategoryService.findCategoryById(stored.category)
        val valueType = indicatorValueTypeService.findValueTypeById<T, C>(stored.valueType)
        return if (category != null && valueType != null) {
            val valueConfig = valueType.fromConfigStoredJson(stored.valueConfig)
            IndicatorType(
                    id = stored.id,
                    category = category,
                    shortName = stored.shortName,
                    longName = stored.longName,
                    link = stored.link,
                    valueType = valueType,
                    valueConfig = valueConfig,
                    valueComputer = null
            )
        } else {
            null
        }
    }

    override fun createType(input: CreateTypeForm): IndicatorType<*, *> {
        val id = UUID.randomUUID().toString()
        return updateType(id, input)
    }

    override fun updateType(id: String, input: CreateTypeForm): IndicatorType<*, *> {
        securityService.checkGlobalFunction(IndicatorTypeManagement::class.java)
        val category = indicatorCategoryService.getCategory(input.category)
        val valueType = indicatorValueTypeService.getValueType<Any, Any>(input.valueType.id)
        val valueConfig = valueType.toConfigStoredJson(
                valueType.fromConfigForm(input.valueType.data ?: NullNode.instance)
        )
        val stored = StoredIndicatorType(
                id = id,
                category = category.id,
                shortName = input.shortName,
                longName = input.longName,
                link = input.link,
                valueType = valueType.id,
                valueConfig = valueConfig
        )
        storageService.store(
                STORE,
                id,
                stored
        )
        return getTypeById(id)
    }

    private class StoredIndicatorType(
            val id: String,
            val category: String,
            val shortName: String,
            val longName: String,
            val link: String?,
            val valueType: String,
            val valueConfig: JsonNode
    )

    companion object {
        private val STORE: String get() = IndicatorType::class.java.name
    }

}