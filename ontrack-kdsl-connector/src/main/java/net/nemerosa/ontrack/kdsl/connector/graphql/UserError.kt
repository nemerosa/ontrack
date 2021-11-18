package net.nemerosa.ontrack.kdsl.connector.graphql

class UserError(
        val message: String,
        val exception: String? = null,
        val location: String? = null
)