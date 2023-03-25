package net.nemerosa.ontrack.extension.tfc

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = TFCConfigProperties.PREFIX)
class TFCConfigProperties(
    var hook: HookConfig = HookConfig()
) {

    class HookConfig(
        var signature: HookSignatureConfig = HookSignatureConfig(),
    )

    /**
     * Hook signature configuration
     *
     * @property disabled Set to `true` to disable the signature checks (OK for testing, NOT for production)
     */
    class HookSignatureConfig(
        var disabled: Boolean = false,
    )

    companion object {
        const val PREFIX = "ontrack.extension.tfc"
    }

}