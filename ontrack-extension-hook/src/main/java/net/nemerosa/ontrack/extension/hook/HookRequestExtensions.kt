package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson

inline fun <reified T : Any> HookRequest.parseParameters(): T =
    parameters.asJson().parse()

inline fun <reified T : Any> HookRequest.parseBodyAsJson(): T =
    body.parseAsJson().parse()
