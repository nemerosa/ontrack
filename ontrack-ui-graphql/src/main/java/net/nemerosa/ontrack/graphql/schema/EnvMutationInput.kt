package net.nemerosa.ontrack.graphql.schema

import graphql.schema.DataFetchingEnvironment
import net.nemerosa.ontrack.graphql.support.getMutationInputField
import net.nemerosa.ontrack.graphql.support.getRequiredMutationInputField

class EnvMutationInput(
    private val env: DataFetchingEnvironment
) : MutationInput {

    override fun <T> getRequiredInput(name: String): T =
        getRequiredMutationInputField(env, name)

    override fun <T> getInput(name: String): T? =
        getMutationInputField(env, name)

}