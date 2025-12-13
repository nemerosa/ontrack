package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class GitHubIngestionHookPayloadsUserMenuItemExtension(
    extensionFeature: GitHubExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(extensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() = listOfNotNull(
            if (securityService.isGlobalFunctionGranted<GlobalSettings>()) {
                UserMenuItem(
                    groupId = CoreUserMenuGroups.SYSTEM,
                    extension = "extension/${feature.id}",
                    id = "ingestion/hook-payloads",
                    name = "GitHub Ingestion Hook Payloads",
                )
            } else {
                null
            }
        )

}