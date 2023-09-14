package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.preferences.PreferencesService
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryPreferences(
    private val gqlTypePreferences: GQLTypePreferences,
    private val preferencesService: PreferencesService,
    private val securityService: SecurityService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("preferences")
            .description("Gets the preferences of the current user")
            .type(gqlTypePreferences.typeRef.toNotNull())
            .dataFetcher {
                // Gets the current account
                val account = securityService.currentAccount?.account ?: error("Authentication is required.")
                // Gets the current preferences
                preferencesService.getPreferences(account)
            }
            .build()
}
