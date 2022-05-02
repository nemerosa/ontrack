package net.nemerosa.ontrack.model.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class APILabel(
        /**
         * Value for the label
         */
        val value: String
)