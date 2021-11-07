package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.model.support.ActionType
import org.springframework.stereotype.Component

@Component
class GitHubIngestionHookPayloadsUserMenuExtension(
    extensionFeature: GitHubExtensionFeature
) : AbstractExtension(extensionFeature), UserMenuExtension {

    override fun getAction() = Action(
        id = "ingestion-hook-payloads",
        name = "GitHub Ingestion Hook Payloads",
        type = ActionType.LINK,
        uri = "ingestion-hook-payloads",
    )

    override fun getGlobalFunction(): Class<out GlobalFunction> = GlobalSettings::class.java
}