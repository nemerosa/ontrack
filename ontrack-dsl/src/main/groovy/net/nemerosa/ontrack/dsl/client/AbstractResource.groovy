package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.ResourceMissingLinkException
import net.nemerosa.ontrack.json.JsonUtils

class AbstractResource {

    protected final Ontrack ontrack
    protected final JsonNode node

    AbstractResource(Ontrack ontrack, JsonNode node) {
        this.ontrack = ontrack
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
        ontrack.get(url).resources as List
    }

    protected JsonNode get(String url) {
        ontrack.get(url)
    }

    protected JsonNode post(String url, data) {
        ontrack.post(url, data)
    }

}
