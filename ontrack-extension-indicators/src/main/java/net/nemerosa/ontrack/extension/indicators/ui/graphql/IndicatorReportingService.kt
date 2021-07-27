package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import net.nemerosa.ontrack.model.structure.Project

interface IndicatorReportingService {
    val arguments: List<GraphQLArgument>

    fun findProjects(env: DataFetchingEnvironment, types: List<IndicatorType<*, *>>): List<Project>
}