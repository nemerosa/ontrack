import {BuildLinksPage} from "./buildLinks";
import {PromotionInfoSection} from "./PromotionInfoSection";
import {confirmBox} from "../../support/confirm";

const {ui} = require("@ontrack/connection");
const {expect} = require("@playwright/test");

export class BuildPage {

    constructor(page, build) {
        this.page = page
        this.build = build
    }

    async goTo() {
        await this.page.goto(`${ui()}/build/${this.build.id}`)
        await expect(this.page.getByText(this.build.name)).toBeVisible()
        // Widgets must be visible
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

}