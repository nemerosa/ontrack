import {BuildLinksPage} from "./buildLinks";
import {PromotionInfoSection} from "./PromotionInfoSection";
import {confirmBox} from "../../support/confirm";

const {expect} = require("@playwright/test");

export class BuildPage {

    constructor(page, build) {
        this.page = page
        this.build = build
    }

    async goTo() {
        await this.page.goto(`${this.build.ontrack.connection.ui}/build/${this.build.id}`)
        await this.assertName(this.build.name)
        // Widgets must be visible
        await this.checkOnBuildPage()
    }

    async checkOnBuildPage() {
        await expect(this.page.getByText("Promotions")).toBeVisible()
        await expect(this.page.getByText("Validations")).toBeVisible()
        await expect(this.page.getByText("Downstream links")).toBeVisible()
        await expect(this.page.getByText("Upstream links")).toBeVisible()
    }

    async goToLinks() {
        await this.page.getByRole("button", {name: "Links"}).click()
        return new BuildLinksPage(this.page)
    }

    async getPromotionInfoSection() {
        const section = this.page.getByTestId('promotions')
        await expect(section).toBeVisible()
        return new PromotionInfoSection(this.page, section, this.build)
    }

    async deleteBuild() {
        const button = this.page.getByRole('button', {name: 'Delete build'})
        await expect(button).toBeVisible()
        await button.click()
        await confirmBox(this.page, "Delete build", {okText: "Delete"})
    }

    async update({name, description}) {
        const button = this.page.getByRole('button', {name: 'Edit'})
        await expect(button).toBeVisible()
        await button.click()

        const buildNameField = this.page.getByPlaceholder('Build name');
        await expect(buildNameField).toBeVisible()
        await buildNameField.fill(name)
        if (description) await this.page.getByPlaceholder('Build description').fill(description)
        await this.page.getByRole('button', {name: 'OK'}).click()
    }

    async assertName(name) {
        await expect(this.page.getByText(name)).toBeVisible()
    }

    async assertDescription(description) {
        await expect(this.page.getByText(description)).toBeVisible()
    }
}