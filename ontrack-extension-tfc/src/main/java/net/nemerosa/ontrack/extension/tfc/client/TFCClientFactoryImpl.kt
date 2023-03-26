package net.nemerosa.ontrack.extension.tfc.client

import net.nemerosa.ontrack.extension.tfc.config.TFCConfiguration
import org.springframework.stereotype.Component

@Component
class TFCClientFactoryImpl : TFCClientFactory {
    override fun createClient(config: TFCConfiguration) = TFCClientImpl(
        url = config.url,
        token = config.token,
    )
}