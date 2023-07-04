package net.nemerosa.ontrack.model.security

data class Authorization(
    val name: String,
    val action: String,
    val authorized: Boolean,
) {

    companion object {

        /**
         * Common actions: creating
         */
        const val CREATE = "create"

        /**
         * Common actions: editing
         */
        const val EDIT = "edit"

    }

}
