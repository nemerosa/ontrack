package net.nemerosa.ontrack.extension.tfc

import net.nemerosa.ontrack.extension.tfc.settings.TFCSettings
import net.nemerosa.ontrack.it.SettingsTestSupport
import org.springframework.stereotype.Component

@Component
class TFCTestSupport(
        private val tfcConfigProperties: TFCConfigProperties,
        private val settingsTestSupport: SettingsTestSupport,
) {

    fun forTesting(code: () -> Unit) {
        withEnabled {
            withNoSignature {
                code()
            }
        }
    }

    fun withEnabled(code: () -> Unit) {
        settingsTestSupport.apply {
            withSettings<TFCSettings> {
                updateSettings<TFCSettings> {
                    TFCSettings(
                            enabled = true,
                            token = it.token,
                    )
                }
                code()
            }
        }
    }

    fun withNoSignature(code: () -> Unit) {
        val old = tfcConfigProperties.hook.signature.disabled
        try {
            tfcConfigProperties.hook.signature.disabled = true
            code()
        } finally {
            tfcConfigProperties.hook.signature.disabled = old
        }
    }
}