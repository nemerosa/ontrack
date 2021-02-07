package net.nemerosa.ontrack.dsl.v4

class RunInfo extends AbstractResource {

    RunInfo(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    int getId() {
        node['id'] as int
    }

    String getSourceType() {
        node['sourceType']
    }

    String getSourceUri() {
        node['sourceUri']
    }

    String getTriggerType() {
        node['triggerType']
    }

    String getTriggerData() {
        node['triggerData']
    }

    int getRunTime() {
        node['runTime'] as int
    }

}
