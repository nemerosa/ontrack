package net.nemerosa.ontrack.dsl.v4

class Property extends AbstractResource {

    Property(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    def update(data) {
        // Gets the update link
        def update = link('update')
        if (update) {
            ontrack.put(update, data)
        } else {
            throw new PropertyNotEditableException(type)
        }
    }
}
