package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.fetcher
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunnableEntity
import org.springframework.stereotype.Component

@Component
class GQLTypeRunInfo(
        private val creation: GQLTypeCreation
) : GQLType {

    companion object {
        const val RUN_INFO = "RunInfo"
    }

    override fun getTypeName() = RUN_INFO

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return newObject()
                .name(RUN_INFO)
                .intField("id", "Unique ID of the run info")
                .stringField("sourceType", "Type of source (like \"jenkins\")")
                .stringField("sourceUri", "URI to the source of the run (like the URL to a Jenkins job)")
                .stringField("triggerType", "Type of trigger (like \"scm\" or \"user\")")
                .stringField("triggerData", "Data associated with the trigger (like a user ID or a commit)")
                .intField("runTime", "Time of the run (in seconds)")
                // val signature: Signature?
                // Creation
                .field {
                    it.name("creation")
                            .type(creation.typeRef)
                            .dataFetcher(GQLTypeCreation.dataFetcher<RunInfo> { it.signature })
                }
                // OK
                .build()
    }
}

inline fun <reified T : RunnableEntity> GraphQLFieldDefinition.Builder.runInfoFetcher(noinline fn: (T) -> RunInfo?): GraphQLFieldDefinition.Builder =
        dataFetcher(
                fetcher(
                        T::class.java,
                        fn
                )
        )