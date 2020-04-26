package net.nemerosa.ontrack.model.support

import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

/**
 * Type safe access to settings.
 */
inline fun <reified T> SettingsRepository.getString(property: KProperty1<T, String?>, defaultValue: String): String =
        getString(T::class.java, property.name, defaultValue)

/**
 * Type safe access to settings.
 */
inline fun <reified T> SettingsRepository.getPassword(
        property: KProperty1<T, String?>,
        defaultValue: String,
        noinline decryptService: (String?) -> String?
): String = getPassword(T::class.java, property.name, defaultValue, decryptService)

/**
 * Type safe setter of settings
 */
inline fun <reified T> SettingsRepository.setString(property: KProperty0<String?>) {
    setString(T::class.java, property.name, property.get())
}
