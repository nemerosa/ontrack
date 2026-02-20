import {expect} from "@playwright/test";

export class AutoVersioningConfigDetails {

    constructor(page, details) {
        this.page = page
        this.details = details
    }

    async expectCronSchedule(cronSchedule) {
        const cell = this.details.getByTestId('auto-versioning-schedule')
        await expect(cell).toHaveText(cronSchedule)
    }

}