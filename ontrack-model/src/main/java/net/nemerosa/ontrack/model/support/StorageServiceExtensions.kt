package net.nemerosa.ontrack.model.support

/**
 * Kotlin friendly method to get a stored object using its class.
 */
inline fun <reified T> StorageService.retrieve(store: String, key: String): T? =
        retrieve(store, key, T::class.java).orElse(null)
