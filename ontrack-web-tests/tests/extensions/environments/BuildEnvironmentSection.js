import {expect} from "@playwright/test";
import {confirmBox} from "../../support/confirm";

export const getBuildEnvironmentSection = async (page, build) => {
    const section = page.getByTestId('environments')
    await expect(section).toBeVisible()
    return new BuildEnvironmentSection(page, section, build)
}

export class BuildEnvironmentSection {

    constructor(page, section, build) {
        this.page = page
        this.section = section
        this.build = build
    }

    async expectBuildDeployButton({environment}) {
        await expect(this.section.getByRole('link', {name: environment.name})).toBeVisible()
        await expect(this.section.getByRole('button', {name: this.build.name})).toBeVisible()
    }

    async buildDeploy({environment}) {
        await this.section.getByRole('button', {name: this.build.name}).click()
        // Confirmation
        await confirmBox(this.page, "Candidate deployment", {okText: "OK"})
        // Expecting to be on the pipeline page, but we need the pipeline ID
        await expect(this.page.getByText(`Slot ${environment.name} - ${this.build.branch.project.name}`)).toBeVisible()
        await expect(this.page.getByRole('link', {name: this.build.name})).toBeVisible()
    }

}