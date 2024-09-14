package net.nemerosa.ontrack.extension.workflows.definition

/**
 * Returns the sum of all the workflow nodes timeout values.
 */
val Workflow.totalTimeout: Long get() = nodes.sumOf { it.timeout }
