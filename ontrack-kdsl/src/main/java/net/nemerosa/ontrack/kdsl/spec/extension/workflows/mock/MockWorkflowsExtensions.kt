package net.nemerosa.ontrack.kdsl.spec.extension.workflows.mock

import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowsMgt

val WorkflowsMgt.mock: MockWorkflowsMgt get() = MockWorkflowsMgt(connector)
