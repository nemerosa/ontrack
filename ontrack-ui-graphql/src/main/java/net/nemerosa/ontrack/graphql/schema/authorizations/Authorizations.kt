package net.nemerosa.ontrack.graphql.schema.authorizations

import kotlin.reflect.KClass

interface Authorizations<T : Any> {

    /**
     * Target type
     */
    val targetType: KClass<T>

    /**
     * Gets a list of authorizations
     */
    val authorizations: List<Authorization<T>>

}