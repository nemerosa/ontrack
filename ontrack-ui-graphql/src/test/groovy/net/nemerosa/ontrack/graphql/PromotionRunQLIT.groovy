package net.nemerosa.ontrack.graphql

import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class PromotionRunQLIT extends AbstractQLITSupport {

    @Test
    void 'Promotion run reference to build and validation stamp'() {
        def pl = doCreatePromotionLevel()
        def branch = pl.branch
        def build = doCreateBuild(branch, nd("1", "Build 1"))
        def prun = doPromote(build, pl, '')
        def data = run("""{
            promotionRuns(id: ${prun.id}) {
                build {
                    id
                }
                promotionLevel {
                    id
                    branch {
                        id
                        project {
                            id
                        }
                    }
                }
            }
        }""")
        def p = data.promotionRuns.first()
        assert p.build.id == build.id()
        assert p.promotionLevel.id == pl.id()
        assert p.promotionLevel.branch.id == pl.branch.id()
        assert p.promotionLevel.branch.project.id == pl.branch.project.id()
    }

}
