package net.nemerosa.ontrack.model.annotations

/**
 * Annotates an API element to give it a name which would be different than the default
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class APIName(
        /**
         * Value for the name
         */
        val value: String
)
