package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class SettingsTestSupport(
        private val cachedSettingsService: CachedSettingsService,
        private val settingsManagerService: SettingsManagerService,
        private val securityService: SecurityService,
) {

    final inline fun <reified S> withSettings(noinline code: () -> Unit) {
        withSettings(S::class.java, code)
    }

    final inline fun <reified S> updateSettings(noinline code: (current: S) -> S) {
        updateSettings(S::class.java, code)
    }

    fun <S> withSettings(type: Class<S>, code: () -> Unit) {
        val old = cachedSettingsService.getCachedSettings(type)
        try {
            code()
        } finally {
            securityService.asAdmin {
                settingsManagerService.saveSettings(old)
            }
        }
    }

    fun <S> updateSettings(type: Class<S>, code: (current: S) -> S) {
        val current = cachedSettingsService.getCachedSettings(type)
        val new = code(current)
        securityService.asAdmin {
            settingsManagerService.saveSettings(new)
        }
    }

}