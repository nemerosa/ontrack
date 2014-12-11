package net.nemerosa.ontrack.dsl

import com.fasterxml.jackson.databind.JsonNode

interface OntrackConnector {

    JsonNode get(String url)

}