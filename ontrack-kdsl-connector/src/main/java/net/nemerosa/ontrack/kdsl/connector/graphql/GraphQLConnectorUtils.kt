package net.nemerosa.ontrack.kdsl.connector.graphql

object GraphQLConnectorUtils {

    fun <T, R> checkData(data: T?, code: (T) -> R): R =
        if (data != null) {
            code(data)
        } else {
            throw GraphQLClientException("No data was returned")
        }

}