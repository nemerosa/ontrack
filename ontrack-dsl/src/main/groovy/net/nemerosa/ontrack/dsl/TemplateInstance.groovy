package net.nemerosa.ontrack.dsl

class TemplateInstance extends AbstractResource {

    TemplateInstance(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    Map<String, String> getParameters() {
        return node.parameterValues.collectEntries {
            [it.name, it.value]
        }
    }
}
