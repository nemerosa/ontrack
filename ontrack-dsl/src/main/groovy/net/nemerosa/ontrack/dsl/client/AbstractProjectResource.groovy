package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.ProjectEntity
import net.nemerosa.ontrack.dsl.PropertyNotFoundException
import net.nemerosa.ontrack.json.JsonUtils

abstract class AbstractProjectResource extends AbstractResource implements ProjectEntity {

    AbstractProjectResource(Ontrack ontrack, JsonNode node) {
        super(ontrack, node)
    }

    int getId() {
        JsonUtils.getInt(node, 'id')
    }

    String getName() {
        return JsonUtils.get(node, 'name')
    }

    String getDescription() {
        return JsonUtils.get(node, 'description')
    }

    @Override
    def properties(Closure closure) {
        closure.delegate = properties
        closure()
    }

    @Override
    def property(String type, Object data) {
        // Gets the list of properties
        def properties = get(link('properties'))
        // Looks for the property
        JsonNode propertyNode = properties.resources.find { it.typeDescriptor.typeName.asText() == type } as JsonNode
        // Found
        if (propertyNode != null) {
            new PropertyResource(ontrack, propertyNode).update(data)
        }
        // Not found
        else {
            throw new PropertyNotFoundException(type)
        }
    }

    @Override
    def property(String type) {
        // Gets the list of properties
        def properties = get(link('properties'))
        // Looks for the property
        JsonNode propertyNode = properties.resources.find { it.typeDescriptor.typeName.asText() == type } as JsonNode
        // Found
        if (propertyNode != null) {
            JsonNode valueNode = propertyNode.path('value')
            if (valueNode.missingNode || valueNode.null) {
                throw new PropertyNotFoundException(type)
            } else {
                JsonUtils.toMap(valueNode)
            }
        }
        // Not found
        else {
            throw new PropertyNotFoundException(type)
        }
    }
}
