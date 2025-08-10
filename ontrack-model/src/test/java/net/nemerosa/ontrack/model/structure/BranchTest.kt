package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Branch.Companion.of
import net.nemerosa.ontrack.model.structure.Project.Companion.of
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test

class BranchTest {

    @Test
    fun collection_without_projects() {
        val project = of(NameDescription("PRJ", "Project"))
        val branches = listOf(
            of(project, NameDescription("B1", "Branch 1")).withSignature(TestFixtures.SIGNATURE),
            of(project, NameDescription("B2", "Branch 2")).withSignature(TestFixtures.SIGNATURE)
        )

        TestUtils.assertJsonWrite(
            listOf(
                mapOf(
                    "id" to 0,
                    "name" to "B1",
                    "description" to "Branch 1",
                    "disabled" to false,
                    "signature" to TestFixtures.SIGNATURE_OBJECT,
                ),
                mapOf(
                    "id" to 0,
                    "name" to "B2",
                    "description" to "Branch 2",
                    "disabled" to false,
                    "signature" to TestFixtures.SIGNATURE_OBJECT,
                )
            ).asJson(),
            branches,
            MutableList::class.java
        )
    }

    @Test
    fun branch_with_project() {
        val project = of(NameDescription("PRJ", "Project")).withSignature(TestFixtures.SIGNATURE)
        val branch = of(project, NameDescription("B", "Branch")).withSignature(TestFixtures.SIGNATURE)

        TestUtils.assertJsonWrite(
            mapOf(
                "id" to 0,
                "name" to "B",
                "description" to "Branch",
                "disabled" to false,
                "project" to mapOf(
                    "id" to 0,
                    "name" to "PRJ",
                    "description" to "Project",
                    "disabled" to false,
                    "signature" to TestFixtures.SIGNATURE_OBJECT,
                ),
                "signature" to TestFixtures.SIGNATURE_OBJECT,
            ).asJson(),
            branch,
            Branch::class.java
        )
    }
}
