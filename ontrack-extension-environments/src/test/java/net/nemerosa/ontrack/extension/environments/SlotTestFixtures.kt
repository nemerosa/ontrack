package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.model.structure.Project
import java.util.*

object SlotTestFixtures {
    fun testSlot(
        id: String = UUID.randomUUID().toString(),
        env: Environment = EnvironmentTestFixtures.testEnvironment(),
        project: Project,
        qualifier: String = Slot.DEFAULT_QUALIFIER,
    ) = Slot(
        id = id,
        environment = env,
        description = null,
        project = project,
        qualifier = qualifier,
    )
}