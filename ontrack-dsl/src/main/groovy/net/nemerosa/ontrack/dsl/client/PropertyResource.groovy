package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.PropertyNotEditableException

class PropertyResource extends AbstractResource {

    PropertyResource(Ontrack ontrack, JsonNode node) {
        super(ontrack, node)
    }

    def update(data) {
        // Gets the update link
        def update = link('update')
        if (update) {
            post(update, data)
        } else {
            throw new PropertyNotEditableException(type)
        }
    }
}
