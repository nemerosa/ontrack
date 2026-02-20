import {generate} from "@ontrack/utils";
import {createMockSCMContext} from "@ontrack/extensions/scm/scm";
import {waitUntilCondition} from "../../support/timing";

export class SimpleAutoVersioning {
    constructor(page, ontrack) {
        this.page = page
        this.ontrack = ontrack
    }

    async init({cronSchedule = null}) {
        // Dependency setup
        const depName = generate("dep-")
        this.depProject = await this.ontrack.createProject(depName)
        this.depBranch = await this.depProject.createBranch("main")
        this.depPromotionLevel = await this.depBranch.createPromotionLevel("GOLD")

        // SCM
        const mockSCMContext = createMockSCMContext(this.ontrack)

        // Target setup
        const targetName = generate("target-")
        this.targetProject = await this.ontrack.createProject(targetName)
        this.targetBranch = await this.targetProject.createBranch("main")

        // Target SCM setup
        await mockSCMContext.configureProjectForMockSCM(this.targetProject)
        await mockSCMContext.configureBranchForMockSCM(this.targetBranch)
        await mockSCMContext.repositoryFile({
            path: "versions.properties",
            content: 'version=1.0.0',
        })

        // Auto-versioning setup
        await this.ontrack.autoVersioning.setAutoVersioningConfig(this.targetBranch, {
            sourceProject: this.depProject.name,
            sourceBranch: 'main',
            sourcePromotion: 'GOLD',
            targetPath: 'versions.properties',
            targetProperty: 'version',
            validationStamp: 'auto',
            cronSchedule: cronSchedule,
        })
    }

    async createDepBuild(name) {
        return this.depBranch.createBuild(name)
    }

    async createTargetBuild(name) {
        return this.targetBranch.createBuild(name)
    }

    async promoteDepBuild(depBuild) {
        await depBuild.promote(this.depPromotionLevel)
    }

    async waitForAutoVersioningCompletion({depBuild, state = 'PR_MERGED'}) {
        let entry = null
        await waitUntilCondition({
            page: this.page,
            condition: async () => {
                const entries = await this.ontrack.autoVersioning.audit.entries({
                    source: this.depProject.name,
                    project: this.targetProject.name,
                    branch: this.targetBranch.name,
                    version: depBuild.name,
                })
                if (entries.length > 0) {
                    const candidate = entries[0]
                    if (candidate.mostRecentState.state === state) {
                        entry = candidate
                        return true
                    } else {
                        return false
                    }
                } else {
                    return false
                }
            },
            message: `Auto-versioning of ${depBuild.name}/${this.depProject.name} in ${this.targetProject.name}`
        })
        if (entry) {
            return entry
        } else {
            throw new Error(`Auto-versioning of ${depBuild.name}/${this.depProject.name} in ${this.targetProject.name} not completed.`)
        }
    }
}

export const setupSimpleAutoVersioning = async ({page, ontrack, cronSchedule = null}) => {
    const setup = new SimpleAutoVersioning(page, ontrack)
    await setup.init({cronSchedule})
    return setup
}
