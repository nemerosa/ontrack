package net.nemerosa.ontrack.model.annotations

/**
 * Specific marker to ignore some elements in an API.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class APIIgnore(
        /**
         * Reason
         */
        val value: String = ""
)