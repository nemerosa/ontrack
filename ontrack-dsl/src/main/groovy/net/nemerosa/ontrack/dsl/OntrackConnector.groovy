package net.nemerosa.ontrack.dsl

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Document

interface OntrackConnector {

    JsonNode get(String url)

    JsonNode post(String url, data)

    JsonNode put(String url, data)

    void upload(String url, String name, Object o)

    void upload(String url, String name, Object o, String contentType)

    Document download(String url)
}