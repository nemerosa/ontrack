package net.nemerosa.ontrack.graphql.support

data class EntityRef(
    @TypeRef(embedded = true)
    val entity: Entity,
)