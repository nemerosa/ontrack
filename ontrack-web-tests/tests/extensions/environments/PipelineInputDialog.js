import {expect} from "@playwright/test";

export class PipelineInputDialog {

    constructor(page) {
        this.page = page
    }

    async manualInput({actions}) {
        const okButton = this.page.getByRole('button', {name: 'OK'});
        await expect(okButton).toBeVisible()

        if (actions) {
            await actions(this.page)
        }

        await okButton.click()
    }

}