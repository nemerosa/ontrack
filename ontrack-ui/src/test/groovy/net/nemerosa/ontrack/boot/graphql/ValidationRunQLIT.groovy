package net.nemerosa.ontrack.boot.graphql

import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test

class ValidationRunQLIT extends AbstractQLITSupport {

    @Test
    void 'Validation run statuses for a validation run'() {
        def vs = doCreateValidationStamp()
        def branch = vs.branch
        def project = branch.project
        def build = doCreateBuild(branch, NameDescription.nd("1", "Build 1"))
        def validationRun = asUser().with(project, ValidationRunCreate).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            0,
                            Signature.of('test'),
                            ValidationRunStatusID.STATUS_FAILED,
                            "Validation failed"
                    )
            )
        }
        asUser().with(project, ValidationRunStatusChange).call {
            structureService.newValidationRunStatus(
                    validationRun,
                    ValidationRunStatus.of(
                            Signature.of('test'),
                            ValidationRunStatusID.STATUS_INVESTIGATING,
                            "Investigating"
                    )
            )
            structureService.newValidationRunStatus(
                    validationRun,
                    ValidationRunStatus.of(
                            Signature.of('test'),
                            ValidationRunStatusID.STATUS_EXPLAINED,
                            "Explained"
                    )
            )
        }
        def data = run("""{
            validationRuns(id: ${validationRun.id}) {
                validationRunStatuses {
                    statusID {
                        id
                    }
                    description
                }
            }
        }""")
        def validationRunStatuses = data.validationRuns.validationRunStatuses.flatten()
        assert validationRunStatuses.statusID.id as Set == ['EXPLAINED', 'INVESTIGATING', 'FAILED'] as Set
        assert validationRunStatuses.description as Set == ['Explained', 'Investigating', 'Validation failed'] as Set
    }

}
