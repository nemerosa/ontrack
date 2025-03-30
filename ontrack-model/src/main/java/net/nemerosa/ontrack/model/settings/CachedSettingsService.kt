package net.nemerosa.ontrack.model.settings

interface CachedSettingsService {

    fun <T> getCachedSettings(type: Class<T>): T
    fun <T> invalidate(type: Class<T>)

}
