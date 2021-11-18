package net.nemerosa.ontrack.kdsl.connector.graphql

fun <T : Any, R> T?.checkData(
    code: (T) -> R,
) = if (this != null) {
    val r = code(this)
    r ?: throw GraphQLClientException("No data node was returned")
} else {
    throw GraphQLClientException("No data was returned")
}
