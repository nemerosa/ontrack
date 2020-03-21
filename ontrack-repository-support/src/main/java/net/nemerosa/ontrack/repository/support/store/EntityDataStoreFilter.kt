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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntityDataStoreFilter) return false

        if (entity != other.entity) return false
        if (category != other.category) return false
        if (name != other.name) return false
        if (group != other.group) return false
        if (beforeTime != other.beforeTime) return false
        if (offset != other.offset) return false
        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        var result = entity?.hashCode() ?: 0
        result = 31 * result + (category?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (group?.hashCode() ?: 0)
        result = 31 * result + (beforeTime?.hashCode() ?: 0)
        result = 31 * result + offset
        result = 31 * result + count
        return result
    }


}