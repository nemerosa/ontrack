package net.nemerosa.ontrack.extension.workflows.registry

import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.yaml.Yaml

object WorkflowYaml {

    fun parseYamlWorkflow(workflowYaml: String): Workflow =
        Yaml().read(workflowYaml)
            .firstOrNull()?.parse()
            ?: throw WorkflowYamlParsingException()

}