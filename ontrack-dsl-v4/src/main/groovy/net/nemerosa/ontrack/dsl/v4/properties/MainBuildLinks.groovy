package net.nemerosa.ontrack.dsl.v4.properties


import net.nemerosa.ontrack.dsl.v4.doc.DSL

@DSL("Configuration which describes the list of build links to display, based on some project labels.")
class MainBuildLinks {
    private final List<String> labels
    private final boolean overrideGlobal

    MainBuildLinks(List<String> labels, boolean overrideGlobal) {
        this.labels = labels
        this.overrideGlobal = overrideGlobal
    }

    List<String> getLabels() {
        return labels
    }

    boolean isOverrideGlobal() {
        return overrideGlobal
    }
}
