package net.nemerosa.ontrack.common.api

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class APIDescription(
        /**
         * Value for the description
         */
        val value: String
)