package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractSCMChangeLogTestSupport : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    protected fun prepareChangeLogTestCase(
        code: (repositoryName: String, from: Build, to: Build) -> Unit,
    ) {
        asAdmin {
            mockSCMTester.withMockSCMRepository {
                project {
                    branch {
                        configureMockSCMBranch()

                        build {}
                        val from = build {
                            // Mock termination commit
                            repositoryIssue("ISS-20", "Last issue before the change log", type = "defect")
                            withRepositoryCommit("ISS-20 Last commit before the change log")
                        }
                        build {
                            repositoryIssue("ISS-21", "Some new feature", type = "feature")
                            withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                            withRepositoryCommit("ISS-21 Some fixes for a feature")
                        }
                        build {
                            repositoryIssue("ISS-22", "Some fixes are needed", type = "defect")
                            withRepositoryCommit("ISS-22 Fixing some bugs")
                        }
                        build {
                            repositoryIssue("ISS-23", "Some nicer UI", type = "enhancement")
                            withRepositoryCommit("ISS-23 Fixing some CSS")

                            code(
                                repositoryName,
                                from,
                                this@build,
                            )

                        }
                    }
                }
            }
        }
    }

}