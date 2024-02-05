const {createMockSCMContext} = require("@ontrack/extensions/scm/scm");
const {ontrack} = require("@ontrack/ontrack");

export async function provisionChangeLog() {
    const mockSCMContext = createMockSCMContext()
    const project = await ontrack().createProject()
    await mockSCMContext.configureProjectForMockSCM(project)

    const branch = await project.createBranch()
    await mockSCMContext.configureBranchForMockSCM(branch)

    await mockSCMContext.repositoryIssue({key: "ISS-20", summary: "Last issue before the change log"})
    await mockSCMContext.repositoryIssue({key: "ISS-21", summary: "Some new feature"})
    await mockSCMContext.repositoryIssue({key: "ISS-22", summary: "Some fixes are needed"})
    await mockSCMContext.repositoryIssue({key: "ISS-23", summary: "Some nicer UI"})

    const from = await mockSCMContext.setBuildWithCommits(
        branch.createBuild(),
        [
            "ISS-20 Last commit before the change log",
        ]
    )

    await mockSCMContext.setBuildWithCommits(
        branch.createBuild(),
        [
            "ISS-21 Some commits for a feature",
            "ISS-21 Some fixes for a feature",
        ],
    )

    await mockSCMContext.setBuildWithCommits(
        branch.createBuild(),
        [
            "ISS-22 Fixing some bugs",
        ]
    )

    const to = await mockSCMContext.setBuildWithCommits(
        branch.createBuild(),
        [
            "ISS-23 Fixing some CSS",
        ]
    )

    return {
        from,
        to,
    }
}

export class SCMChangeLogPage {

    constructor(page) {
        this.page = page
    }

    async checkDisplayed() {
        // TODO
    }

}
