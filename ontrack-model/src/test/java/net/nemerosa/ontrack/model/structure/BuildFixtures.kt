package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.Time
import java.time.LocalDateTime

object BuildFixtures {

    fun testBuild(
        branch: Branch = BranchFixtures.testBranch(),
        name: String = "1",
        dateTime: LocalDateTime = Time.now,
    ) = Build(
        id = ID.of(1000),
        name = name,
        description = null,
        signature = Signature.of(dateTime = dateTime, name = "test"),
        branch = branch,
    )

}