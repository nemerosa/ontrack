package net.nemerosa.ontrack.common

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor

/**
 * Checks if a given property has a default value.
 *
 * @receiver Property to check
 * @param kClass The parent class of the property
 * @return True if the property has a default value
 */
fun KProperty1<*, *>.hasDefaultValue(kClass: KClass<*>): Boolean {
    val constructor = kClass.primaryConstructor ?: return false
    return constructor.parameters
        .firstOrNull { it.name == this.name }
        ?.isOptional == true
}
