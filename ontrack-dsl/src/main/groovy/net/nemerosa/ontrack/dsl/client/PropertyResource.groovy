package net.nemerosa.ontrack.dsl.client

import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.PropertyNotEditableException

class PropertyResource extends AbstractResource {

    PropertyResource(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    def update(data) {
        // Gets the update link
        def update = link('update')
        if (update) {
            put(update, data)
        } else {
            throw new PropertyNotEditableException(type)
        }
    }
}
