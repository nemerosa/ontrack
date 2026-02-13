import {expect} from "@playwright/test";
import {AbstractImagePage} from "../common/AbstractImagePage";
import {AutoVersioningTrail} from "../../extensions/auto-versioning/AutoVersioningTrail";

export class PromotionLevelPage extends AbstractImagePage {

    constructor(page, promotionLevel) {
        super(page)
        this.promotionLevel = promotionLevel
    }

    id() {
        return `promotion-level-image-${this.promotionLevel.id}`
    }

    async goTo() {
        await this.page.goto(`${this.promotionLevel.ontrack.connection.ui}/promotionLevel/${this.promotionLevel.id}`)
        await expect(this.page.getByText(this.promotionLevel.name)).toBeVisible()
    }

    async getAVTrail() {
        const section = this.page.getByTestId('auto-versioning-trail')
        await expect(section).toBeVisible()
        return new AutoVersioningTrail(this.page, section)
    }

}