package net.nemerosa.ontrack.model.structure

inline fun <reified P : PropertyType<T>, T> PropertyService.forEachEntityWithProperty(
        noinline code: (entityId: ProjectEntityID, property: T) -> Unit
) = forEachEntityWithProperty(P::class.java, code)
