package net.nemerosa.ontrack.model.annotations

import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

fun getDescription(property: KProperty<*>, defaultDescription: String? = null): String =
    defaultDescription
        ?: property.findAnnotation<APIDescription>()?.value
        ?: "${property.name} property"
