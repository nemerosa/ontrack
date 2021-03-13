package net.nemerosa.ontrack.graphql.schema

/**
 * Abstraction for the input for a GraphQL mutation.
 */
interface MutationInput {

    /**
     * Gets an input by name
     */
    fun <T> getRequiredInput(name: String): T

    /**
     * Gets an optional input by name
     */
    fun <T> getInput(name: String): T?

}