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

    /**
     * List of decorations for this entity.
     *
     * Each item in the list has a:
     *
     * <ul>
     *     <li><code>decorationType</code> - qualified name of the decoration
     *     <li><code>data</code> - JSON data of the decoration
     * </ul>
     */
    List<?> getDecorations() {
        return ontrack.get(link('decorations')).resources;
    }

    /**
     * Gets the decoration for this entity and a given decoration qualified name.
     */
    List<?> getDecorations(String type) {
        return decorations
                .findAll { it.decorationType == type }
                .collect { it.data }
    }

    /**
     * Gets the first decoration for this entity and a given decoration qualified name.
     *
     * Returns <code>null</code> if not defined or the JSON data is defined.
     */
    def getDecoration(String type) {
        return getDecorations(type).first()
    }

    /**
     * Message decoration. Defines a <code>type</code> and a <code>text</code>
     */
    List<?> getMessageDecoration() {
        getDecorations('net.nemerosa.ontrack.extension.general.MessageDecorationExtension')
    }

    /**
     * Jenkins job decoration (state of the job)
     */
    String getJenkinsJobDecoration() {
        getDecoration('net.nemerosa.ontrack.extension.jenkins.JenkinsJobDecorationExtension') as String
    }

}
