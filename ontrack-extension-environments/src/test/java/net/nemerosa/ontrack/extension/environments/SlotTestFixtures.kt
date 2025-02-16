package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildFixtures
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

    fun testDeployment(
        id: String = UUID.randomUUID().toString(),
        number: Int = 1,
        build: Build = BuildFixtures.testBuild(),
        slot: Slot = testSlot(project = build.project),
    ) = SlotPipeline(
        id = id,
        number = number,
        slot = slot,
        build = build,
    )
}