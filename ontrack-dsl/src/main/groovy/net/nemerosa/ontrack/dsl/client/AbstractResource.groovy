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
        String linkName = '_' + name
        if (node.has(linkName)) {
            JsonUtils.get(node, linkName)
        } else {
            throw new ResourceMissingLinkException(name);
        }
    }

    protected def post(String url, data) {
        connector.post(url, data)
    }

}
