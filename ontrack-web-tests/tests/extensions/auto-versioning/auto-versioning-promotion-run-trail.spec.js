const {test} = require("../../fixtures/connection");
const {generate} = require("@ontrack/utils");
const {createMockSCMContext} = require("@ontrack/extensions/scm/scm");
const {waitUntilCondition} = require("../../support/timing");
const {PromotionRunPage} = require("../../core/promotionRuns/PromotionRunPage");
const {login} = require("../../core/login");
const {PromotionLevelPage} = require("../../core/promotionLevels/PromotionLevelPage");

test('filtering the AV trail for a promotion run', async ({page, ontrack}) => {
    const {
        targetProject,
        secondaryTargetProject,
        promotionRun,
    } = await setupAutoVersioningTrail({page, ontrack})

    // Getting to the promotion run page
    await login(page, ontrack)
    const promotionRunPage = new PromotionRunPage(page, promotionRun)
    await promotionRunPage.goTo()

    // Getting the AV trail
    const avTrail = await promotionRunPage.getAVTrail()

    // Checking the AV trail
    await checkAutoVersioningTrail({
        avTrail,
        targetProject,
        secondaryTargetProject,
    })
})

test('filtering the AV trail for a promotion level', async ({page, ontrack}) => {
    const {
        targetProject,
        secondaryTargetProject,
        promotionRun,
    } = await setupAutoVersioningTrail({page, ontrack})

    // Getting to the promotion level page
    await login(page, ontrack)
    const promotionLevelPage = new PromotionLevelPage(page, promotionRun.promotionLevel)
    await promotionLevelPage.goTo()

    // Getting the AV trail
    const avTrail = await promotionLevelPage.getAVTrail()

    // Checking the AV trail
    await checkAutoVersioningTrail({
        avTrail,
        targetProject,
        secondaryTargetProject,
    })
})

const checkAutoVersioningTrail = async ({avTrail, targetProject, secondaryTargetProject}) => {
    // Checking that only the eligible AV config are displayed
    await avTrail.expectAVTrailVisible({project: targetProject.name, branch: "v20", visible: false})
    await avTrail.expectAVTrailVisible({project: targetProject.name, branch: "v21", visible: false})
    await avTrail.expectAVTrailVisible({project: targetProject.name, branch: "v22", visible: true})
    await avTrail.expectAVTrailVisible({project: secondaryTargetProject.name, branch: "main", visible: true})

    // Filter off on eligibility
    await avTrail.filterByEligibility({onlyEligible: false})
    await avTrail.expectAVTrailVisible({project: targetProject.name, branch: "v20", visible: true})
    await avTrail.expectAVTrailVisible({project: targetProject.name, branch: "v21", visible: true})
    await avTrail.expectAVTrailVisible({project: targetProject.name, branch: "v22", visible: true})
    await avTrail.expectAVTrailVisible({project: secondaryTargetProject.name, branch: "main", visible: true})

    // Restoring the eligibility filter
    await avTrail.filterByEligibility({onlyEligible: true})

    // Filtering on project name
    await avTrail.filterByProjectName({projectName: targetProject.name})
    await avTrail.expectAVTrailVisible({project: targetProject.name, branch: "v20", visible: false})
    await avTrail.expectAVTrailVisible({project: targetProject.name, branch: "v21", visible: false})
    await avTrail.expectAVTrailVisible({project: targetProject.name, branch: "v22", visible: true})
    await avTrail.expectAVTrailVisible({project: secondaryTargetProject.name, branch: "main", visible: false})
}

const setupAutoVersioningTrail = async ({page, ontrack}) => {
    const depName = generate("dep-")
    const depProject = await ontrack.createProject(depName)
    const depBranch = await depProject.createBranch("main")
    const depPromotionLevel = await depBranch.createPromotionLevel("GOLD")

    const mockSCMContext = createMockSCMContext(ontrack)

    const targetName = generate("target-")
    const targetProject = await ontrack.createProject(targetName)
    await mockSCMContext.configureProjectForMockSCM(targetProject)

    // Several target branches, one of them being disabled
    const versions = ["v20", "v21", "v22"]
    for (const version of versions) {
        const targetBranch = await targetProject.createBranch(version)
        if (version !== "v22") {
            await targetBranch.disableBranch()
        }
        // SCM setup of the target(s)
        await mockSCMContext.configureBranchForMockSCM(targetBranch, version)
        await mockSCMContext.repositoryFile({
            path: "versions.properties",
            branch: version,
            content: 'version=1.0.0',
        })
        // Setting up the auto-versioning dep --> target
        await ontrack.autoVersioning.setAutoVersioningConfig(targetBranch, {
            sourceProject: depProject.name,
            sourceBranch: 'main',
            sourcePromotion: 'GOLD',
            targetPath: 'versions.properties',
            targetProperty: 'version',
            validationStamp: 'auto',
        })
    }

    // Secondary target, on a different project
    const secondaryTargetProject = await ontrack.createProject(generate("secondary-target-"))
    const secondaryMockSCMContext = createMockSCMContext(ontrack)
    await secondaryMockSCMContext.configureProjectForMockSCM(secondaryTargetProject)
    const secondaryTargetBranch = await secondaryTargetProject.createBranch("main")
    // SCM setup of the target(s)
    await secondaryMockSCMContext.configureBranchForMockSCM(secondaryTargetBranch, "main")
    await secondaryMockSCMContext.repositoryFile({
        path: "versions.properties",
        branch: "main",
        content: 'version=1.0.0',
    })
    // Setting up the auto-versioning dep --> secondary target
    await ontrack.autoVersioning.setAutoVersioningConfig(secondaryTargetBranch, {
        sourceProject: depProject.name,
        sourceBranch: 'main',
        sourcePromotion: 'GOLD',
        targetPath: 'versions.properties',
        targetProperty: 'version',
        validationStamp: 'auto',
    })

    // Promoting the source
    const depBuild = await depBranch.createBuild("2.0.0")
    const promotionRun = await depBuild.promote(depPromotionLevel)
    await waitUntilCondition({
        page,
        condition: async () => {
            const entries = await ontrack.autoVersioning.audit.entries({
                source: depProject.name,
                project: targetProject.name,
                version: depBuild.name,
            })
            if (entries.length > 0) {
                return entries.every(it => it.mostRecentState.state === 'PR_MERGED')
            } else {
                return false
            }
        },
        message: `Auto-versioning of ${depBuild.name}/${depProject.name} in ${targetProject.name}`
    })

    return {
        targetProject,
        secondaryTargetProject,
        promotionRun,
    }
}