package net.nemerosa.ontrack.extension.tfc

import org.springframework.stereotype.Component

@Component
class TFCTestSupport(
    private val tfcConfigProperties: TFCConfigProperties,
) {

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