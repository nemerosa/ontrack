package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL

@DSL("Label")
class Label extends AbstractResource {
    Label(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }
}
