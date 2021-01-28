package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IndicatorCategoryServiceImpl(
    private val securityService: SecurityService,
    private val storageService: StorageService
) : IndicatorCategoryService {

    private val listeners = mutableListOf<IndicatorCategoryListener>()

    override fun registerCategoryListener(listener: IndicatorCategoryListener) {
        listeners += listener
    }

    override fun createCategory(input: IndicatorForm, source: IndicatorSource?): IndicatorCategory {
        securityService.checkGlobalFunction(IndicatorTypeManagement::class.java)
        val type = findCategoryById(input.id)
        if (type != null) {
            throw IndicatorCategoryIdAlreadyExistsException(input.id)
        } else {
            return updateCategory(input, source)
        }
    }

    override fun updateCategory(input: IndicatorForm, source: IndicatorSource?): IndicatorCategory {
        securityService.checkGlobalFunction(IndicatorTypeManagement::class.java)
        val stored = StoredIndicatorCategory(
            id = input.id,
            name = input.name,
            source = source,
            deprecated = input.deprecated?.takeIf { it.isNotBlank() }
        )
        storageService.store(
            STORE,
            input.id,
            stored
        )
        return getCategory(input.id)
    }

    override fun deleteCategory(id: String, force: Boolean): Ack {
        securityService.checkGlobalFunction(IndicatorTypeManagement::class.java)
        if (force) securityService.checkGlobalFunction(GlobalSettings::class.java)
        val category = findCategoryById(id)
        return if (category != null) {
            if (force || category.source == null || !category.deprecated.isNullOrBlank()) {
                listeners.forEach { it.onCategoryDeleted(category) }
                storageService.delete(STORE, id)
                Ack.OK
            } else {
                Ack.NOK
            }
        } else {
            Ack.NOK
        }
    }

    override fun deprecateCategory(id: String, deprecated: String?) {
        securityService.checkGlobalFunction(IndicatorTypeManagement::class.java)
        val existing = storageService.retrieve(STORE, id, StoredIndicatorCategory::class.java).getOrNull()
        if (existing != null) {
            val deprecatedEntry = existing.withDeprecated(deprecated)
            storageService.store(
                STORE,
                id,
                deprecatedEntry
            )
        }
    }

    override fun findCategoryById(id: String): IndicatorCategory? =
        storageService.retrieve(STORE, id, StoredIndicatorCategory::class.java)
            .getOrNull()
            ?.let { fromStorage(it) }

    override fun getCategory(id: String): IndicatorCategory {
        return findCategoryById(id) ?: throw IndicatorCategoryNotFoundException(id)
    }

    override fun findAll(): List<IndicatorCategory> {
        return storageService.getKeys(STORE).mapNotNull { key ->
            storageService.retrieve(STORE, key, StoredIndicatorCategory::class.java).getOrNull()
        }.map {
            fromStorage(it)
        }.sortedBy { it.name }
    }

    private fun fromStorage(stored: StoredIndicatorCategory): IndicatorCategory =
        IndicatorCategory(
            id = stored.id,
            name = stored.name,
            source = stored.source,
            deprecated = stored.deprecated
        )

    private class StoredIndicatorCategory(
        val id: String,
        val name: String,
        val source: IndicatorSource?,
        val deprecated: String?
    ) {
        fun withDeprecated(deprecated: String?) = StoredIndicatorCategory(
            id,
            name,
            source,
            deprecated
        )
    }

    companion object {
        private val STORE: String = IndicatorCategory::class.java.name
    }

}