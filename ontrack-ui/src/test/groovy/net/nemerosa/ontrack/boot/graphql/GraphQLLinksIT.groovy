package net.nemerosa.ontrack.boot.graphql

import net.nemerosa.ontrack.graphql.AbstractQLITSupport
import net.nemerosa.ontrack.model.security.ProjectEdit
import org.junit.Test

class GraphQLLinksIT extends AbstractQLITSupport {

    @Test
    void 'Validation stamp image link'() {
        def vs = doCreateValidationStamp()
        def data = run("""{
            branches (id: ${vs.branch.id}) {
                validationStamps {
                    links {
                        _image
                    }
                }
            }
        }""")
        assert data.branches.first().validationStamps.first().links._image == "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampController#getValidationStampImage_:,${vs.id}"
    }

    @Test
    void 'Promotion level image link'() {
        def pl = doCreatePromotionLevel()
        def data = run("""{
            branches (id: ${pl.branch.id}) {
                promotionLevels {
                    links {
                        _image
                    }
                }
            }
        }""")
        assert data.branches.first().promotionLevels.first().links._image == "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevelImage_:,${pl.id}"
    }

    @Test
    void 'Branch links'() {
        def branch = doCreateBranch()

        def data = run("""{branches (id: ${branch.id}) { name links { _page } } }""")
        assert data.branches.first().name == branch.name
        assert data.branches.first().links._page == "urn:test:#:entity:BRANCH:${branch.id}"
    }

    @Test
    void 'Project links'() {
        def p = doCreateProject()
        def data = asUser().with(p, ProjectEdit).call { run("{projects(id: ${p.id}) { name links { _update } }}") }
        assert data.projects.first().name == p.name
        assert data.projects.first().links._update == "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#saveProject:${p.id},"
    }
}
