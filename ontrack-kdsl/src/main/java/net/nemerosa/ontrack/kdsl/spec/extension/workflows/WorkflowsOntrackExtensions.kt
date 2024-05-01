package net.nemerosa.ontrack.kdsl.spec.extension.workflows

import net.nemerosa.ontrack.kdsl.spec.Ontrack

/**
 * Management of workflows in Ontrack.
 */
val Ontrack.workflows: WorkflowsMgt get() = WorkflowsMgt(connector)