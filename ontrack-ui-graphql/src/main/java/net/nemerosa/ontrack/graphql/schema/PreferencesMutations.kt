package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.graphql.support.getDescription
import net.nemerosa.ontrack.model.preferences.Preferences
import net.nemerosa.ontrack.model.preferences.PreferencesService
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component
import javax.validation.Validator

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
                branchViewLegacy = input.branchViewLegacy ?: current.branchViewLegacy,
                branchViewVsNames = input.branchViewVsNames ?: current.branchViewVsNames,
                branchViewVsGroups = input.branchViewVsGroups ?: current.branchViewVsGroups,
            )
            // Saves the preferences...
            preferencesService.setPreferences(account, new)
            // ... and returns them
            new
        }

    )

}

@Component
class GQLTypePreferences : GQLType {

    override fun getTypeName(): String = Preferences::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getDescription(Preferences::class))
            .fields(GraphQLBeanConverter.asObjectFields(Preferences::class, cache))
            .build()

}

data class SetPreferencesInput(
    val branchViewLegacy: Boolean? = null,
    val branchViewVsNames: Boolean? = null,
    val branchViewVsGroups: Boolean? = null,
)