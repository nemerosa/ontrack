package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorStatus
import net.nemerosa.ontrack.extension.indicators.model.scale.Scales
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicator
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import org.springframework.stereotype.Component

typealias TypeBuilder = GraphQLObjectType.Builder

@Component
class GQLTypeScaleValues : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name("ScaleValues")
                    .description("Conversion of indicator statuses to different scales")
                    .fields(
                            Scales.scales.map { scaleFactory ->
                                GraphQLFieldDefinition.newFieldDefinition()
                                        .name(scaleFactory.name)
                                        .description(scaleFactory.description)
                                        .type(GraphQLString)
                                        .dataFetcher { env ->
                                            val status: IndicatorStatus? = env.getSource<IndicatorStatus>()
                                            status?.value?.let {
                                                scaleFactory.toScale(it).toString()
                                            }
                                        }
                                        .build()
                            }
                    )
                    .build()

    override fun getTypeName(): String = "ScaleValues"
}

fun TypeBuilder.scaleValues(scaleValues: GQLTypeScaleValues): GraphQLObjectType.Builder =
        field {
            it.name("scales")
                    .description("Scales for the status")
                    .type(scaleValues.typeRef)
                    .dataFetcher { env ->
                        env.getSource<ProjectIndicator>().status
                    }
        }
