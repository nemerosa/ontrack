package net.nemerosa.ontrack.extension.hook.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.hook.HookEndpointExtension
import net.nemerosa.ontrack.extension.hook.records.HookRecordState
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.toNotNull
import org.springframework.stereotype.Component

/**
 * Getting information for the hook record filtering.
 */
@Component
class GQLRootQueryHookRecordFilterInfo(
        private val gqlTypeHookRecordFilterInfo: GQLTypeHookRecordFilterInfo,
        private val extensionManager: ExtensionManager,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("hookRecordFilterInfo")
                    .description("Information for the hook record query filter")
                    .type(gqlTypeHookRecordFilterInfo.typeRef.toNotNull())
                    .dataFetcher {
                        HookRecordFilterInfo(
                                hooks = extensionManager.getExtensions(HookEndpointExtension::class.java).map { it.id }.sorted(),
                                states = HookRecordState.values().map { it.name },
                        )
                    }
                    .build()

}
