package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.OntrackConnector
import net.nemerosa.ontrack.dsl.ResourceMissingLinkException
import net.nemerosa.ontrack.json.JsonUtils

class AbstractResource {

    protected final OntrackConnector connector
    protected final JsonNode node

    AbstractResource(OntrackConnector connector, JsonNode node) {
        this.connector = connector
        this.node = node
    }

    protected String link(String name) {
        String linkName = name.startsWith('_') ? name : '_' + name
        if (node.has(linkName)) {
            JsonUtils.get(node, linkName)
        } else {
            throw new ResourceMissingLinkException(name);
        }
    }

    protected static String query(String url, Map<String, ?> parameters) {
        if (parameters.empty) {
            url
        } else {
            "${url}?${parameters.collect { k, v -> "$k=$v" }.join('?')}"
        }
    }

    protected List<JsonNode> list(String url) {
        connector.get(url).resources as List
    }

    protected JsonNode get(String url) {
        connector.get(url)
    }

    protected JsonNode post(String url, data) {
        connector.post(url, data)
    }

}
