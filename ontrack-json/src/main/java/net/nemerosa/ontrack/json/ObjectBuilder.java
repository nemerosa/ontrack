package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectBuilder implements JsonBuilder<ObjectNode> {

    private final JsonNodeFactory factory;
    private final ObjectNode thisNode;

    public ObjectBuilder(JsonNodeFactory factory) {
        this.factory = factory;
        this.thisNode = factory.objectNode();
    }

    public ObjectBuilder withNull(String field) {
        return with(field, factory.nullNode());
    }

    public ObjectBuilder with(String field, int value) {
        return with(field, factory.numberNode(value));
    }

    public ObjectBuilder with(String field, boolean value) {
        return with(field, factory.booleanNode(value));
    }

    public ObjectBuilder with(String field, JsonNode node) {
        thisNode.set(field, node);
        return this;
    }

    public ObjectBuilder with(String field, String value) {
        return with(field, factory.textNode(value));
    }

    public ObjectNode end() {
        return thisNode;
    }
}
