package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.structure.NameDescription
import org.junit.Test
import kotlin.test.assertEquals

class BranchGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Last promotion run only`() {
        // Creates a branch
        val branch = doCreateBranch()
        // ... a promotion level
        val pl = doCreatePromotionLevel(branch, NameDescription.nd("COPPER", ""))
        // ... one build
        val build = doCreateBuild(branch, NameDescription.nd("1", ""))
        // ... and promotes it twice
        doPromote(build, pl, "Once")
        doPromote(build, pl, "Twice")
        // Asks for the promotion runs of the build
        val data = run("""{
            |branches(id: ${branch.id}) {
            |   builds {
            |       promotionRuns(lastPerLevel: true) {
            |           promotionLevel {
            |               name
            |           }
            |           description
            |       }
            |   }
            |}
            |}""".trimMargin())
        // Gets the first build
        val buildNode = data.path("branches").get(0).path("builds").get(0)
        // Gets the promotion runs
        val promotionRuns = buildNode.path("promotionRuns")
        assertEquals(1, promotionRuns.size())
        val promotionRun = promotionRuns.get(0)
        assertEquals("Twice", promotionRun.path("description").asText())
        assertEquals("COPPER", promotionRun.path("promotionLevel").path("name").asText())
    }

}