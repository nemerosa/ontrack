package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.model.Ack
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

    override fun createCategory(input: IndicatorForm): IndicatorCategory {
        securityService.checkGlobalFunction(IndicatorTypeManagement::class.java)
        val type = findCategoryById(input.id)
        if (type != null) {
            throw IndicatorCategoryIdAlreadyExistsException(input.id)
        } else {
            return updateCategory(input)
        }
    }

    override fun updateCategory(input: IndicatorForm): IndicatorCategory {
        securityService.checkGlobalFunction(IndicatorTypeManagement::class.java)
        val stored = StoredIndicatorCategory(
                id = input.id,
                name = input.name
        )
        storageService.store(
                STORE,
                input.id,
                stored
        )
        return getCategory(input.id)
    }

    override fun deleteCategory(id: String): Ack {
        securityService.checkGlobalFunction(IndicatorTypeManagement::class.java)
        storageService.delete(STORE, id)
        return Ack.OK
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
        }.mapNotNull {
            fromStorage(it)
        }.sortedBy { it.name }
    }

    private fun fromStorage(stored: StoredIndicatorCategory): IndicatorCategory? =
            IndicatorCategory(
                    id = stored.id,
                    name = stored.name,
                    source = null
            )

    private class StoredIndicatorCategory(
            val id: String,
            val name: String
    )

    companion object {
        private val STORE: String = IndicatorCategory::class.java.name
    }

}