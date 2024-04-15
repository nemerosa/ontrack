package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumAsStringField
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.longField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.job.Schedule
import org.springframework.stereotype.Component

@Component
class GQLTypeSchedule : GQLType {

    override fun getTypeName(): String = Schedule::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(Schedule::class))
            .longField(Schedule::initialPeriod)
            .longField(Schedule::period)
            .enumAsStringField(Schedule::unit)
            .stringField(Schedule::cron)
            .stringField(Schedule::periodText)
            .build()
}