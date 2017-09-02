package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Before
import org.junit.Test

class BuildValidationsQLIT extends AbstractQLITSupport {

    private Build build

    @Before
    void init() {
        // Creates a build
        build = doCreateBuild()
        // Gets the branch...
        def branch = build.branch
        // ... and creates a few validation stamps
        def vs1 = doCreateValidationStamp(branch, NameDescription.nd("VS1", ""))
        def vs2 = doCreateValidationStamp(branch, NameDescription.nd("VS2", ""))
        doCreateValidationStamp(branch, NameDescription.nd("VS3", ""))
        // ... validates the build
        doValidateBuild(build, vs1, ValidationRunStatusID.STATUS_PASSED)
        doValidateBuild(build, vs2, ValidationRunStatusID.STATUS_FAILED)
        doValidateBuild(build, vs2, ValidationRunStatusID.STATUS_PASSED)
    }

    @Test
    void 'All validations for a build'() {
        def data = run("""{
            builds(id: ${build.id}) {
               name
               validations {
                   validationStamp {
                       name
                   }
                   validationRuns {
                       validationRunStatuses {
                           statusID {
                               id
                           }
                       }
                   }
               }
            }
        }""")
        def validations = data.builds.first().validations
        assert validations != null
        assert validations.size() == 3
        assert validations*.validationStamp.name == ['VS1', 'VS2', 'VS3']

        assert validations.getAt(0).validationRuns*.validationRunStatuses*.statusID.id == [['PASSED']]
        assert validations.getAt(1).validationRuns*.validationRunStatuses*.statusID.id == [['PASSED'], ['FAILED']]
        assert validations.getAt(2).validationRuns*.validationRunStatuses*.statusID.id == []
    }

    @Test
    void 'Validations for a build and a validation stamp'() {
        def data = run("""{
            builds(id: ${build.id}) {
               name
               validations(validationStamp: "VS1") {
                   validationStamp {
                       name
                   }
                   validationRuns {
                       validationRunStatuses {
                           statusID {
                               id
                           }
                       }
                   }
               }
            }
        }""")
        def validations = data.builds.first().validations
        assert validations != null
        assert validations.size() == 1
        assert validations*.validationStamp.name == ['VS1']

        assert validations.getAt(0).validationRuns*.validationRunStatuses*.statusID.id == [['PASSED']]
    }

    @Test
    void 'Validations for a build with limited count of runs'() {
        def data = run("""{
            builds(id: ${build.id}) {
               name
               validations {
                   validationStamp {
                       name
                   }
                   validationRuns(count: 1) {
                       validationRunStatuses {
                           statusID {
                               id
                           }
                       }
                   }
               }
            }
        }""")
        def validations = data.builds.first().validations
        assert validations != null
        assert validations.size() == 3
        assert validations*.validationStamp.name == ['VS1', 'VS2', 'VS3']

        assert validations.getAt(0).validationRuns*.validationRunStatuses*.statusID.id == [['PASSED']]
        assert validations.getAt(1).validationRuns*.validationRunStatuses*.statusID.id == [['PASSED']]
        assert validations.getAt(2).validationRuns*.validationRunStatuses*.statusID.id == []
    }

}