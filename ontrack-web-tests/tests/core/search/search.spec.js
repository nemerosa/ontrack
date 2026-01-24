const {test} = require("../../fixtures/connection");
const {login} = require("../login");
const {HomePage} = require("../home/home");
const {ProjectPage} = require("../projects/project");
const {createMockSCMContext} = require("@ontrack/extensions/scm/scm");
const {SCMCommitPage} = require("./SCMCommitPage");
const {generate} = require("@ontrack/utils");
const {SCMIssuePage} = require("./SCMIssuePage");

test('searching for a project', async ({page, ontrack}) => {
    await login(page, ontrack)

    const project = await ontrack.createProject()

    const homePage = new HomePage(page, ontrack)
    const searchPage = await homePage.search(project.name)

    await searchPage.expectProjectResultPresent(project.name)

    await searchPage.clickProjectResult(project.name)

    const projectPage = new ProjectPage(page, ontrack, project)
    await projectPage.expectOnPage()
})

test('searching for a commit', async ({page, ontrack}) => {
    const mockSCMContext = createMockSCMContext(ontrack)
    const project = await ontrack.createProject()
    await mockSCMContext.configureProjectForMockSCM(project)
    const branch = await project.createBranch()
    const build = await branch.createBuild()
    await mockSCMContext.configureBranchForMockSCM(branch)

    const commitMessage = "Build commit message"
    const branchName = generate("b-")

    const commitId = await mockSCMContext.repositoryCommit({branch: branchName, message: commitMessage})
    console.log(`Commit ID: ${commitId}`)
    await mockSCMContext.configureBuildForMockSCM(build, commitId)

    await ontrack.search.forceIndexation({type: "scm-commit"})

    await login(page, ontrack)

    const homePage = new HomePage(page, ontrack)
    const searchPage = await homePage.search(commitId)

    await searchPage.expectScmCommitResultPresent({commitId})

    await searchPage.clickScmCommitResult({commitId})

    const scmCommitPage = new SCMCommitPage(page, ontrack, commitId, project)
    await scmCommitPage.expectOnPage(commitMessage)
})

test('searching for an issue', async ({page, ontrack}) => {
    const mockSCMContext = createMockSCMContext(ontrack)
    const project = await ontrack.createProject()
    await mockSCMContext.configureProjectForMockSCM(project)
    const branch = await project.createBranch()
    const build = await branch.createBuild()
    await mockSCMContext.configureBranchForMockSCM(branch)

    const issueKey = generate("ISS-")
    console.log(`Issue key: ${issueKey}`)
    const issueTitle = "Sample issue"
    const commitMessage = `${issueKey} Build commit message`
    const branchName = generate("b-")

    await mockSCMContext.repositoryIssue({key: issueKey, summary: issueTitle, type: "defect"})
    const commitId = await mockSCMContext.repositoryCommit({branch: branchName, message: commitMessage})
    await mockSCMContext.configureBuildForMockSCM(build, commitId)

    await ontrack.search.forceIndexation({type: "scm-commit"}) // Includes the indexation of issues

    await login(page, ontrack)

    const homePage = new HomePage(page, ontrack)
    const searchPage = await homePage.search(issueKey)

    await searchPage.expectScmIssueResultPresent({issueKey})

    await searchPage.clickScmIssueResult({issueKey})

    const scmIssuePage = new SCMIssuePage(page, ontrack, issueKey, project)
    await scmIssuePage.expectOnPage({issueTitle, commitId})
})
