package net.nemerosa.ontrack.graphql.schema.actions

import kotlin.reflect.KClass

interface UIActionsProvider<T : Any> {
    /**
     * Target type
     */
    val targetType: KClass<T>

    /**
     * Gets a list of actions
     */
    val actions: List<UIAction<T>>
}
