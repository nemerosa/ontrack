import {expect} from "@playwright/test";

export class PromotionLevelPage {

    constructor(page, promotionLevel) {
        this.page = page
        this.promotionLevel = promotionLevel
    }

    async goTo() {
        await this.page.goto(`${this.promotionLevel.ontrack.connection.ui}/promotionLevel/${this.promotionLevel.id}`)
        await expect(this.page.getByText(this.promotionLevel.name)).toBeVisible()
    }

    async changeImage(path) {
        await this.page.getByRole('button', {name: "Change image"}).click()

        const upload = this.page.locator('input[type="file"]').first()
        await expect(upload).toBeDefined()
        await upload.setInputFiles(path)

        const ok = this.page.getByRole('button', {name: "OK"})
        await expect(ok).toBeVisible()
        await expect(ok).toBeEnabled()
        await ok.click()
    }

    async checkImage() {
        const id = `promotion-level-image-${this.promotionLevel.id}`
        const image = this.page.getByTestId(id)

        await expect(image).toBeVisible()

        const src = await image.getAttribute('src')
        expect(src).not.toBeNull()
        expect(src).toMatch(/^data:image\//)

        const loaded = await image.evaluate((img) => img.complete && img.naturalWidth > 0)
        expect(loaded).toBe(true)
    }

}