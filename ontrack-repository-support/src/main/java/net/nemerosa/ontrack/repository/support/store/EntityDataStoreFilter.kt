package net.nemerosa.ontrack.repository.support.store

import net.nemerosa.ontrack.model.structure.ProjectEntity
import java.time.LocalDateTime

class EntityDataStoreFilter
@JvmOverloads
constructor(
        val entity: ProjectEntity? = null,
        val category: String? = null,
        val name: String? = null,
        val group: String? = null,
        val beforeTime: LocalDateTime? = null,
        val offset: Int = 0,
        val count: Int = 20
) {
    fun withEntity(entity: ProjectEntity?) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            offset,
            count
    )

    fun withCategory(category: String?) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            offset,
            count
    )

    fun withName(name: String?) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            offset,
            count
    )

    fun withGroup(group: String?) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            offset,
            count
    )

    fun withBeforeTime(beforeTime: LocalDateTime?) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            offset,
            count
    )

    fun withOffset(offset: Int) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            offset,
            count
    )

    fun withCount(count: Int) = EntityDataStoreFilter(
            entity,
            category,
            name,
            group,
            beforeTime,
            offset,
            count
    )
}