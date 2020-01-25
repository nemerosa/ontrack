package net.nemerosa.ontrack.model.structure

suspend inline fun <reified P : PropertyType<T>, T> PropertyService.forEachEntityWithProperty(
        noinline code: (entityId: ProjectEntityID, property: T) -> Unit
) = forEachEntityWithProperty(P::class, code)
