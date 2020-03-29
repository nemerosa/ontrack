package net.nemerosa.ontrack.model.structure

/**
 * Marks a class which should be considered as `open` by the
 * Kotlin `all-open` compiler plugin. This is used to mark
 * classes that can be returned as-is (without being wrapped)
 * by Spring MVC controller and used to generate links.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class OpenEntity {
}