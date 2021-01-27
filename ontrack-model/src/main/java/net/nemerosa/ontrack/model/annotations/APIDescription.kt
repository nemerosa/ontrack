package net.nemerosa.ontrack.model.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class APIDescription(
        /**
         * Value for the description
         */
        val value: String
)