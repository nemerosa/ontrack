package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL
abstract class AbstractProjectResource extends AbstractResource implements ProjectEntity {

    AbstractProjectResource(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Returns the numeric ID of this entity.")
    int getId() {
        node['id'] as int
    }

    @DSLMethod("Returns the name of this entity.")
    String getName() {
        node['name']
    }

    @DSLMethod("Returns any description attached to this entity.")
    String getDescription() {
        node['description']
    }

    @DSLMethod("Configures this entity.")
    @Override
    def config(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = config
        closure()
    }

    @DSLMethod("Sets the value for a property of this entity. Prefer using dedicated DSL methods.")
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

    @DSLMethod(id = "property-get")
    @Override
    def getProperty(String type, boolean required) {
        // Gets the list of properties
        def properties = ontrack.get(link('properties'))
        // Looks for the property
        def propertyNode = properties.resources.find { it.typeDescriptor.typeName as String == type }
        // Found
        if (propertyNode != null) {
            def valueNode = propertyNode['value']
            if (valueNode) {
                valueNode
            } else if (required) {
                throw new PropertyNotFoundException(type)
            } else {
                return null
            }
        }
        // Not found
        else if (required) {
            throw new PropertyNotFoundException(type)
        } else {
            return null
        }
    }

    @DSLMethod(id = "property-get-required")
    @Override
    def property(String type) {
        return getProperty(type, true)
    }

    /**
     * Deletes this entity
     */
    @DSLMethod("Deletes this entity.")
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
    @DSLMethod("Returns the list of decorations for this entity. Each item has a `decorationType` type name and `data` as JSON.")
    List<?> getDecorations() {
        return ontrack.get(link('decorations')).resources;
    }

    /**
     * Gets the decoration for this entity and a given decoration qualified name.
     */
    @DSLMethod(value = "Returns the list of decoration data (JSON) for a given decoration type.", id = "decorations-type")
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
    @DSLMethod("Gets the data for the first decoration of a given type. If no decoration is available, returns null.")
    def getDecoration(String type) {
        def decorations = getDecorations(type)
        return decorations.empty ? null : decorations[0]
    }

    /**
     * Message decoration. Defines a <code>type</code> and a <code>text</code>
     */
    @DSLMethod("Gets any message for this entity")
    Map<String, ?> getMessageDecoration() {
        getDecoration('net.nemerosa.ontrack.extension.general.MessageDecorationExtension') as Map
    }

    /**
     * Jenkins job decoration (state of the job)
     */
    @DSLMethod("Gets the Jenkins decoration for this entity.")
    String getJenkinsJobDecoration() {
        getDecoration('net.nemerosa.ontrack.extension.jenkins.JenkinsJobDecorationExtension') as String
    }

}
