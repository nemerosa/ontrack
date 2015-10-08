package net.nemerosa.ontrack.dsl

abstract class AbstractProjectResource extends AbstractResource implements ProjectEntity {

    AbstractProjectResource(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    int getId() {
        node['id'] as int
    }

    String getName() {
        node['name']
    }

    String getDescription() {
        node['description']
    }

    @Override
    def config(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = config
        closure()
    }

    @Override
    def property(String type, Object data) {
        // Gets the list of properties
        def properties = ontrack.get(link('properties'))
        // Looks for the property
        def propertyNode = properties.resources.find { it.typeDescriptor.typeName as String == type }
        // Found
        if (propertyNode != null) {
            new Property(ontrack, propertyNode).update(data)
        }
        // Not found
        else {
            throw new PropertyNotFoundException(type)
        }
    }

    @Override
    def property(String type) {
        // Gets the list of properties
        def properties = ontrack.get(link('properties'))
        // Looks for the property
        def propertyNode = properties.resources.find { it.typeDescriptor.typeName as String == type }
        // Found
        if (propertyNode != null) {
            def valueNode = propertyNode['value']
            if (valueNode) {
                valueNode
            } else {
                throw new PropertyNotFoundException(type)
            }
        }
        // Not found
        else {
            throw new PropertyNotFoundException(type)
        }
    }

    /**
     * Deletes this entity
     */
    def delete() {
        ontrack.delete(link('delete'))
    }
}
