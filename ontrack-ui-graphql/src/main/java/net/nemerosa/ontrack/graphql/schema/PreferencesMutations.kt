package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.preferences.Preferences
import net.nemerosa.ontrack.model.preferences.PreferencesService
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component
import jakarta.validation.Validator

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
            val account = securityService.currentAccount?.account ?: error("Authentication is required.")
            // Gets the current preferences
            val current = preferencesService.getPreferences(account)
            // Adapts the preferences
            val new = Preferences(
                branchViewVsNames = input.branchViewVsNames ?: current.branchViewVsNames,
                branchViewVsGroups = input.branchViewVsGroups ?: current.branchViewVsGroups,
                dashboardUuid = input.dashboardUuid ?: current.dashboardUuid,
                selectedBranchViewKey = input.selectedBranchViewKey ?: current.selectedBranchViewKey,
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
)