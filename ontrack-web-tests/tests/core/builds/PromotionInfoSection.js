import {expect} from "@playwright/test";
import {PromotionRunDialog} from "../promotionRuns/PromotionRunDialog";

export class PromotionInfoSection {

    constructor(page, section, build) {
        this.page = page
        this.section = section
        this.build = build
    }

    async repromote(run) {
        const runLink = this.section.getByTestId(`build-promote-${this.build.id}-${run.promotionLevel.id}`)
        await expect(runLink).toBeVisible()
        await runLink.click()
        const dialog = new PromotionRunDialog(this.page)
        await dialog.createPromotionRun()
    }

    async checkPromotionRunCount(promotionLevel, expectedCount) {
        const links = this.section.locator(`.promotion-run-pl-${promotionLevel.id}`)
        await expect(links).toHaveCount(expectedCount)
    }

}