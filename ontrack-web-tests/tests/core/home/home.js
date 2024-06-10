import {LegacyHomePage} from "../legacy/legacyHome";

export class HomePage {

    constructor(page) {
        this.page = page
    }

    async legacyHome() {
        await this.page.getByRole('link', {name: 'Legacy home'}).click()
        return new LegacyHomePage(this.page)
    }

}