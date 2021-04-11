package net.nemerosa.ontrack.casc

import com.fasterxml.jackson.databind.JsonNode

interface CascContext {

    fun run(node: JsonNode, paths: List<String>)

}