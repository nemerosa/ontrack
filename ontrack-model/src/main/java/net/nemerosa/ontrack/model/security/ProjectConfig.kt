package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.labels.ProjectLabelManagement

/**
 * Configuration for a project.
 */
@CoreFunction
interface ProjectConfig : ProjectView,
        ProjectLabelManagement,
        ValidationStampFilterCreate,
        ValidationStampFilterShare,
        ValidationStampFilterMgt
