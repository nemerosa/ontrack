package net.nemerosa.ontrack.model.structure

fun EntityStore.store(entity: ProjectEntity, store: String, entityStoreRecord: EntityStoreRecord) =
    store(entity, store, entityStoreRecord.name, entityStoreRecord)

inline fun <reified T : Any> EntityStore.findByName(entity: ProjectEntity, entityStore: String, name: String): T? =
    findByName(entity, entityStore, name, T::class)

inline fun <reified T : Any> EntityStore.getByFilter(
    entity: ProjectEntity,
    store: String,
    offset: Int = 0,
    size: Int = 10,
    jsonFilter: EntityStoreFilter
): List<T> =
    getByFilter(entity, store, offset, size, jsonFilter, T::class)

inline fun <reified T : Any> EntityStore.forEachByFilter(
    entity: ProjectEntity,
    store: String,
    filter: EntityStoreFilter,
    noinline code: (T) -> Unit
) = forEachByFilter(
    entity = entity,
    store = store,
    type = T::class,
    filter = filter,
    code = code
)
