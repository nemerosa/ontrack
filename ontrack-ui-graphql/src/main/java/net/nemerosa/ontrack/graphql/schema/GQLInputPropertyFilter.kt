package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asInputType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asObject
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component
import java.util.function.Predicate

@Component
class GQLInputPropertyFilter(
    private val propertyService: PropertyService
) : GQLInputType<PropertyFilter> {

    override fun getTypeRef(): GraphQLTypeReference = GraphQLTypeReference(PropertyFilter::class.java.simpleName)

    override fun createInputType(dictionary: MutableSet<GraphQLType>): GraphQLInputType = asInputType(PropertyFilter::class, dictionary)

    override fun convert(argument: Any?): PropertyFilter? =
        asObject(
            argument,
            PropertyFilter::class.java
        )

    fun asArgument(): GraphQLArgument = GraphQLArgument.newArgument()
        .name(ARGUMENT_NAME)
        .description("Filter on property type and optional value pattern.")
        .type(GraphQLTypeReference(PropertyFilter::class.java.simpleName))
        .build()

    fun getFilter(filter: PropertyFilter): Predicate<in ProjectEntity> =
        Predicate { e: ProjectEntity -> matchProperty<Any>(e, filter) }

    private fun <T> matchProperty(e: ProjectEntity, filter: PropertyFilter): Boolean {
        val type = propertyService.getPropertyTypeByName<T>(filter.type!!)
        val property = propertyService.getProperty<T>(e, filter.type!!)
        val filterValue = filter.value
        return !property.isEmpty &&
                (filterValue.isNullOrBlank() ||
                        type.containsValue(property.value, filterValue))
    }

    companion object {
        const val ARGUMENT_NAME = "withProperty"
    }
}