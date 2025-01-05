import {expect} from "@playwright/test";
import {PromotionRunDialog} from "./PromotionRunDialog";

export class PromotionRunPopover {

    constructor(page, run) {
        this.page = page
        this.run = run
        this.popover = this.page.getByTestId(`promotion-run-popover-${this.run.id}`)
        this.promoteButton = this.popover.getByTestId(`build-promote-${this.run.build.id}-${this.run.promotionLevel.id}`)
    }

    async checkContent() {
        await expect(this.popover).toBeVisible()

        await expect(this.popover.getByRole('link', {name: this.run.promotionLevel.name})).toBeVisible()
        await expect(this.popover.getByText('Promoted by admin')).toBeVisible()

        await expect(this.popover.getByTestId(`build-promotion-delete-${this.run.id}`)).toBeVisible()
        await expect(this.promoteButton).toBeVisible()
    }

    async repromote() {
        await this.promoteButton.click()
        const dialog = new PromotionRunDialog(this.page)
        await dialog.createPromotionRun()
    }

}