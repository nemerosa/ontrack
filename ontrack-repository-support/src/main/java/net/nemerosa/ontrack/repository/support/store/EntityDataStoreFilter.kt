package net.nemerosa.ontrack.repository.support.store

import net.nemerosa.ontrack.model.structure.ProjectEntity
import java.time.LocalDateTime

data class EntityDataStoreFilter
@JvmOverloads
constructor(
        val entity: ProjectEntity? = null,
        val category: String? = null,
        val name: String? = null,
        val group: String? = null,
        val beforeTime: LocalDateTime? = null,
        val jsonFilter: String? = null,
        val jsonFilterCriterias: Map<String, String>? = null,
        val offset: Int = 0,
        val count: Int = 20
) {
    fun withEntity(entity: ProjectEntity?) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            jsonFilter,
            jsonFilterCriterias,
            offset,
            count
    )

    fun withCategory(category: String?) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            jsonFilter,
            jsonFilterCriterias,
            offset,
            count
    )

    fun withName(name: String?) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            jsonFilter,
            jsonFilterCriterias,
            offset,
            count
    )

    fun withGroup(group: String?) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            jsonFilter,
            jsonFilterCriterias,
            offset,
            count
    )

    fun withBeforeTime(beforeTime: LocalDateTime?) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            jsonFilter,
            jsonFilterCriterias,
            offset,
            count
    )

    fun withOffset(offset: Int) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            jsonFilter,
            jsonFilterCriterias,
            offset,
            count
    )

    fun withCount(count: Int) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            jsonFilter,
            jsonFilterCriterias,
            offset,
            count
    )

    fun withJsonFilter(jsonFilter: String, vararg criterias: Pair<String, String>) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            jsonFilter,
            mapOf(*criterias),
            offset,
            count
    )

}