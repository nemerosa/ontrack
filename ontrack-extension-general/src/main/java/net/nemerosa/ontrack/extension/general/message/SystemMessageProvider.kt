package net.nemerosa.ontrack.extension.general.message

import net.nemerosa.ontrack.extension.api.GlobalMessageExtension
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.message.Message
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component

@Component
class SystemMessageProvider(
    extensionFeature: GeneralExtensionFeature,
    private val cachedSettingsService: CachedSettingsService,
) : AbstractExtension(extensionFeature), GlobalMessageExtension {

    override val globalMessages: List<Message>
        get() = cachedSettingsService.getCachedSettings(SystemMessageSettings::class.java)
            .takeIf { !it.content.isNullOrBlank() }
            ?.let { settings ->
                listOf(
                    Message(
                        content = settings.content!!,
                        type = settings.type,
                    )
                )
            }
            ?: emptyList()
}