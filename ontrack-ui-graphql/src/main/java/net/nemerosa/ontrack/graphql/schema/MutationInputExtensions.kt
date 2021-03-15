package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse

inline fun <reified T> MutationInput.getInputObject(name: String): T? =
    getInput<Any>(name)?.asJson()?.parse<T>()
