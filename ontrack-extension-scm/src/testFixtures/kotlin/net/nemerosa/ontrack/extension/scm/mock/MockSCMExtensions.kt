package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Branch

@Deprecated("Use methods in MockSCMTester")
fun AbstractDSLTestSupport.mockScm(
    branch: Branch,
    scmBranch: String = "main",
) {
    setProperty(branch.project, net.nemerosa.ontrack.extension.scm.mock.MockSCMProjectPropertyType::class.java, MockSCMProjectProperty(branch.project.name))
    setProperty(branch, net.nemerosa.ontrack.extension.scm.mock.MockSCMBranchPropertyType::class.java, MockSCMBranchProperty(scmBranch))
}
