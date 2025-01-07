package net.nemerosa.ontrack.extension.environments

enum class SlotPipelineChangeType {

    /**
     * Status change
     */
    STATUS,

    /**
     * Rule data updated
     */
    RULE_DATA,

    /**
     * Rule overridden
     */
    RULE_OVERRIDDEN,

    /**
     * Workflow overridden
     */
    WORKFLOW_OVERRIDDEN,

}