import {expect} from "@playwright/test";
import {PromotionRunPopover} from "../promotionRuns/PromotionRunPopover";

export class PromotionInfoSection {

    constructor(page, section, build) {
        this.page = page
        this.section = section
        this.build = build
    }

    async showPromotionRun(run) {
        const runLink = this.section.getByTestId(`promotion-run-link-${run.id}`)
        await expect(runLink).toBeVisible()
        await runLink.hover()

        const promotionRunPopover = new PromotionRunPopover(this.page, run)
        await promotionRunPopover.checkContent()
        return promotionRunPopover
    }

    async checkPromotionRunCount(promotionLevel, expectedCount) {
        const links = this.section.locator(`.promotion-run-pl-${promotionLevel.id}`)
        await expect(links).toHaveCount(expectedCount)
    }

}