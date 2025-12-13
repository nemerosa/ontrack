package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.api.Error

class GraphQLClientException(message: String) : RuntimeException(message) {

    companion object {

        fun errors(errors: List<Error>) = GraphQLClientException(
            message = errorsMessage(errors)
        )

        private fun errorsMessage(errors: List<Error>): String =
            errors.map { errorMessage(it) }.joinToString("\n") { "* $it" }

        private fun errorMessage(error: Error): String {
            val base = error.message
            val locations = error.locations ?: emptyList()
            return if (locations.isNotEmpty()) {
                "$base. Locations: ${locations.joinToString { location -> locationMessage(location) }}"
            } else {
                "$base."
            }
        }

        private fun locationMessage(location: Error.Location) =
            "${location.line},${location.column}"

    }

}