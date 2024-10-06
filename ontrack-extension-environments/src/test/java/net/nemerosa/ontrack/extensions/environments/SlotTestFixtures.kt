package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.model.structure.Project
import java.util.*

object SlotTestFixtures {
    fun testSlot(
        env: Environment = EnvironmentTestFixtures.testEnvironment(),
        project: Project,
        qualifier: String? = null,
    ) = Slot(
        id = UUID.randomUUID().toString(),
        environment = env,
        description = null,
        project = project,
        qualifier = qualifier,
        admissionRules = emptyList(),
        deployed = null,
        candidate = null,
    )
}