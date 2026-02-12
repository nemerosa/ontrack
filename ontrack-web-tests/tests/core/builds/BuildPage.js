import {BuildLinksPage} from "./buildLinks";
import {PromotionInfoSection} from "./PromotionInfoSection";
import {confirmBox} from "../../support/confirm";
import {BuildLinksSection} from "./BuildLinksSection";

const {expect} = require("@playwright/test");

export class BuildPage {

    constructor(page, build) {
        this.page = page
        this.build = build
    }

    async goTo() {
        await this.page.goto(`${this.build.ontrack.connection.ui}/build/${this.build.id}`)
        await this.checkOnBuildPage()
    }

    async checkOnBuildPage() {
        await expect(this.page.getByText("Promotions")).toBeVisible()
        await expect(this.page.getByText("Validations")).toBeVisible()
        await expect(this.page.getByText("Downstream links")).toBeVisible()
        await expect(this.page.getByText("Upstream links")).toBeVisible()
        await this.assertName(this.build.name)
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
        await expect(this.page.getByText(name).nth(0)).toBeVisible()
    }

    async assertDescription(description) {
        await expect(this.page.getByText(description)).toBeVisible()
    }

    async expectPreviousBuild({visible = true}) {
        await expect(this.page.getByRole('button', {name: 'Previous build'})).toBeVisible({visible})
    }

    async expectNextBuild({visible = true}) {
        await expect(this.page.getByRole('button', {name: 'Next build'})).toBeVisible({visible})
    }

    async nextBuild() {
        await this.page.getByRole('button', {name: 'Next build'}).click()
    }

    async previousBuild() {
        await this.page.getByRole('button', {name: 'Previous build'}).click()
    }

    async getDownstreamLinks() {
        const downstreamLinks =  this.page.getByTestId('links-using')
        await expect(downstreamLinks).toBeVisible()
        return new BuildLinksSection(this.page, this.build, downstreamLinks)
    }

    async getUpstreamLinks() {
        const upstreamLinks =  this.page.getByTestId('links-usedby')
        await expect(upstreamLinks).toBeVisible()
        return new BuildLinksSection(this.page, this.build, upstreamLinks)
    }

}