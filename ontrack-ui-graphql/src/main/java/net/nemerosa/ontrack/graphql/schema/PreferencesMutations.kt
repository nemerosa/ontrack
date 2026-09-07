package net.nemerosa.ontrack.graphql.schema

import jakarta.validation.Validator
import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.preferences.Preferences
import net.nemerosa.ontrack.model.preferences.PreferencesService
import net.nemerosa.ontrack.model.preferences.ThemeMode
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component

/**
 * Settings the current's user preferences.
 */
@Component
class PreferencesMutations(
    validator: Validator,
    private val preferencesService: PreferencesService,
    private val securityService: SecurityService,
) : TypedMutationProvider(validator) {
    override val mutations: List<Mutation> = listOf(

        simpleMutation(
            name = "setPreferences",
            description = "Setting the preferences of the current user",
            input = SetPreferencesInput::class,
            outputName = "preferences",
            outputDescription = "Saved preferences",
            outputType = Preferences::class,
        ) { input ->
            // Gets the current account
            val account = securityService.currentUser?.account
                ?: throw AccessDeniedException("Not authenticated")
            // Gets the current preferences
            val current = preferencesService.getPreferences(account)
            // Adapts the preferences
            val new = Preferences(
                branchViewVsNames = input.branchViewVsNames ?: current.branchViewVsNames,
                branchViewVsGroups = input.branchViewVsGroups ?: current.branchViewVsGroups,
                dashboardUuid = input.dashboardUuid ?: current.dashboardUuid,
                selectedBranchViewKey = input.selectedBranchViewKey ?: current.selectedBranchViewKey,
                themeMode = input.themeMode ?: current.themeMode,
                selectedChangeLogViewKey = input.selectedChangeLogViewKey ?: current.selectedChangeLogViewKey,
                changeLogSemanticFormat = input.changeLogSemanticFormat ?: current.changeLogSemanticFormat,
                changeLogSemanticEmojis = input.changeLogSemanticEmojis ?: current.changeLogSemanticEmojis,
                changeLogSemanticIssues = input.changeLogSemanticIssues ?: current.changeLogSemanticIssues,
                changeLogSemanticCommits = input.changeLogSemanticCommits ?: current.changeLogSemanticCommits,
            )
            // Saves the preferences...
            preferencesService.setPreferences(account, new)
            // ... and returns them
            new
        }

    )

}

data class SetPreferencesInput(
    val branchViewVsNames: Boolean? = null,
    val branchViewVsGroups: Boolean? = null,
    @APIDescription("Dashboard selected by default")
    var dashboardUuid: String? = null,
    @APIDescription("Selected branch view")
    var selectedBranchViewKey: String? = null,
    @APIDescription("Theme selected for the web UI")
    var themeMode: ThemeMode? = null,
    @APIDescription("Selected change log view")
    var selectedChangeLogViewKey: String? = null,
    @APIDescription("Renderer used by the semantic change log view")
    var changeLogSemanticFormat: String? = null,
    @APIDescription("Emojis in the section titles of the semantic change log")
    var changeLogSemanticEmojis: Boolean? = null,
    @APIDescription("Issues section inside the rendered semantic change log")
    var changeLogSemanticIssues: Boolean? = null,
    @APIDescription("Commits shown beside the semantic change log")
    var changeLogSemanticCommits: Boolean? = null,
)