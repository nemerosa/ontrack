package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

@DSL("Label")
class Label extends AbstractResource {
    Label(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("ID of the label.")
    int getId() {
        node['id'] as int
    }

    @DSLMethod("Category of the label.")
    String getCategory() {
        node['category']
    }

    @DSLMethod("Name of the label.")
    String getName() {
        node['name']
    }

    @DSLMethod("Description of the label.")
    String getDescription() {
        node['description']
    }

    @DSLMethod("Color of the label.")
    String getColor() {
        node['color']
    }
}
