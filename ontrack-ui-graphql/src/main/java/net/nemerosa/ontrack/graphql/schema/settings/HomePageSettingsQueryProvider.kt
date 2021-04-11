package net.nemerosa.ontrack.graphql.schema.settings

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.HomePageSettings
import org.springframework.stereotype.Component

@Component
class HomePageSettingsQueryProvider(
    cachedSettingsService: CachedSettingsService
) : AbstractSettingsQueryProvider<HomePageSettings>(
    cachedSettingsService,
    HomePageSettings::class
) {

    override fun fields(): List<GraphQLFieldDefinition> = listOf(
        GraphQLFieldDefinition.newFieldDefinition()
            .name(HomePageSettings::maxBranches.name)
            .description("Maximum of branches to display per favorite project")
            .type(GraphQLNonNull(GraphQLInt))
            .build(),
        GraphQLFieldDefinition.newFieldDefinition()
            .name(HomePageSettings::maxProjects.name)
            .description("Maximum of projects starting from which we need to switch to a search mode")
            .type(GraphQLNonNull(GraphQLInt))
            .build()
    )

    override val id: String = "homePage"

    override val description: String = "Home page settings"

}