package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class ValidationRunsPaginationQLIT extends AbstractQLITSupport {

    private Branch branch

    @Before
    void setup() {
        branch = doCreateBranch()
        def vs = doCreateValidationStamp(branch, nd('VS', ''))
        (1..100).each { buildNo ->
            def build = doCreateBuild(branch, nd("${buildNo}", ''))
            doValidateBuild(
                    build,
                    vs,
                    ValidationRunStatusID.STATUS_PASSED
            )
        }
    }

    @Test
    void 'Getting all validation runs is limited by default to 50'() {
        def data = run("""{
            branches (id: ${branch.id}) {
                validationStamps(name: "VS") {
                    validationRuns {
                        build {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().validationStamps.first().validationRuns.size() == 50
    }

    @Test
    void 'Getting 20 first runs'() {
        def data = run("""{
            branches (id: ${branch.id}) {
                validationStamps(name: "VS") {
                    validationRuns(count: 20) {
                        build {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().validationStamps.first().validationRuns.size() == 20
    }

}
