package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class StructureServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private StructureService structureService

    /**
     * Regression test for #76.
     *
     * When a build X (created before a build Y) is promoted after Y, this is still Y which should appear as
     * the last promotion.
     */
    @Test
    void '#76 Make sure the promotion run order depends on the build and not the promotion run creation'() {

        // Creates a promotion level
        def promotionLevel = doCreatePromotionLevel()
        def branch = promotionLevel.branch

        // Creates two builds
        def build1 = doCreateBuild(branch, nd("1", "Build 1"))
        def build2 = doCreateBuild(branch, nd("2", "Build 2"))

        // Promotes build 2 BEFORE build 1
        doPromote(build2, promotionLevel, "Promotion of build 2")

        // Promotes build 1 AFTER build 2
        doPromote(build1, promotionLevel, "Promotion of build 1")

        // Gets the last promotion run for the promotion level
        def run = asUser().with(branch.projectId(), ProjectView).call({
            structureService.getLastPromotionRunForPromotionLevel(promotionLevel)
        })
        assert run != null
        assert run.build.id == build2.id: "Build 2 must be the last promoted"

    }

}
