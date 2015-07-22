package net.nemerosa.ontrack.dsl

class BuildLink extends AbstractResource {

    BuildLink(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getProject() {
        node['project']
    }

    String getBuild() {
        node['build']
    }

    String getPage() {
        node['_buildPage']
    }

}
