package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.job.JobCategory
import net.nemerosa.ontrack.job.JobScheduler
import org.springframework.stereotype.Component

@Component
class GQLTypeJobCategory(
    private val gqlTypeJobType: GQLTypeJobType,
    private val jobScheduler: JobScheduler,
) : GQLType {

    override fun getTypeName(): String = JobCategory::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(JobCategory::class))
            .stringField(JobCategory::key)
            .stringField(JobCategory::name)
            .field {
                it.name("types")
                    .description("All job types for this category")
                    .type(listType(gqlTypeJobType.typeRef))
                    .dataFetcher { env ->
                        val category: JobCategory = env.getSource()
                        val types = jobScheduler.allJobKeys
                            .map { key -> key.type }
                            .distinctBy { type -> type.key }
                        types
                            .filter { type -> type.category.key == category.key }
                            .sortedBy { type -> type.name }
                    }
            }
            .build()
}