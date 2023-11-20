package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Branch

fun AbstractDSLTestSupport.mockScm(
    branch: Branch,
    scmBranch: String = "main",
) {
    setProperty(branch.project, MockSCMProjectPropertyType::class.java, MockSCMProjectProperty(branch.project.name))
    setProperty(branch, MockSCMBranchPropertyType::class.java, MockSCMBranchProperty(scmBranch))
}
