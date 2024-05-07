package net.nemerosa.ontrack.model.docs

import kotlin.reflect.KClass

/**
 * Used by a class instead of using [Documentation] when the documentation is carried by itself.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SelfDocumented
