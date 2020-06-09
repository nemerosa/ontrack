package net.nemerosa.ontrack.graphql.schema.authorizations

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.model.security.ProjectFunction
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import kotlin.reflect.KClass

fun <T : Any> GraphQLObjectType.Builder.authorizations(authorizationsService: AuthorizationsService, type: KClass<T>): GraphQLObjectType.Builder {
    val field = authorizationsService.authorizationsField(type)
    if (field != null) {
        field(field)
    }
    return this
}

inline fun <reified F : ProjectFunction> SecurityService.isProjectFunctionGranted(e: ProjectEntity) =
        isProjectFunctionGranted(e, F::class.java)
