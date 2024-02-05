import {expect} from "@playwright/test";

const {createMockSCMContext} = require("@ontrack/extensions/scm/scm");
const {ontrack} = require("@ontrack/ontrack");

export const commits = [
    "ISS-20 Last commit before the change log",
    "ISS-21 Some commits for a feature",
    "ISS-21 Some fixes for a feature",
    "ISS-22 Fixing some bugs",
    "ISS-23 Fixing some CSS",
]

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

    const builds = []

    const from = await mockSCMContext.setBuildWithCommits(
        branch.createBuild(),
        commits.slice(0, 1)
    )
    builds.push(from)

    builds.push(
        await mockSCMContext.setBuildWithCommits(
            branch.createBuild(),
            commits.slice(1, 3)
        )
    )

    builds.push(
        await mockSCMContext.setBuildWithCommits(
            branch.createBuild(),
            commits.slice(3, 4)
        )
    )

    const to = await mockSCMContext.setBuildWithCommits(
        branch.createBuild(),
        commits.slice(4)
    )
    builds.push(to)

    return {
        from,
        to,
        mockSCMContext,
        builds,
    }
}

export class SCMChangeLogPage {

    constructor(page) {
        this.page = page
    }

    async checkDisplayed() {
        await expect(this.page.getByText("Change log from")).toBeVisible()
    }

    async checkBuildFrom({name}) {
        const container = this.page.locator('#from')
        await expect(container.getByText(`From ${name}`, {exact: true})).toBeVisible()
        await expect(container.getByRole('link', {name: name})).toBeVisible()
    }

    async checkBuildTo({name}) {
        const container = this.page.locator('#to')
        await expect(container.getByText(`To ${name}`, {exact: true})).toBeVisible()
        await expect(container.getByRole('link', {name: name})).toBeVisible()
    }

    async checkCommitMessage(message, {present = true}) {
        const container = this.page.locator('#commits')
        const locator = container.getByText(message, {exact: false})
        if (present) {
            await expect(locator).toBeVisible()
        } else {
            await expect(locator).not.toBeVisible()
        }
    }

    async checkCommitBuild(message, mockSCMContext, build, {expected = true}) {
        const commitId = mockSCMContext.commitIdsPerMessage[message]
        const commitLocator = this.page.locator(`#commit-${commitId}`)
        await expect(commitLocator.getByText(message)).toBeVisible()
        const buildLinkLocator = commitLocator.getByRole('link', {name: build.name});
        if (expected) {
            await expect(buildLinkLocator).toBeVisible()
        } else {
            await expect(buildLinkLocator).not.toBeVisible()
        }
    }

}
