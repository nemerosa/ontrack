package net.nemerosa.ontrack.kdsl.acceptance.tests

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.Project

abstract class AbstractACCDSLTestSupport : AbstractACCTestSupport() {

    /**
     * Wrapper code to create a project.
     *
     * @param name Name of the project to create (generated by default)
     * @param description Description of the project
     * @param code Code to run against the project
     */
    protected fun <T> project(
        name: String = uid("p"),
        description: String = "",
        code: Project.() -> T,
    ): T = ontrack.createProject(name, description).code()

    /**
     * Wrapper code to create a branch inside a project.
     *
     * @param name Name of the branch to create (generated by default)
     * @param description Description of the branch
     * @param code Code to run against the branch
     */
    protected fun <T> Project.branch(
        name: String = uid("b"),
        description: String = "",
        code: Branch.() -> T,
    ): T = createBranch(name, description).code()

}