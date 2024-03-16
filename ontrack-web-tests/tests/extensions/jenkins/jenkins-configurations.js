import {ui} from "@ontrack/connection";
import {expect} from "@playwright/test";

export class JenkinsConfigurationsPage {

    constructor(page) {
        this.page = page
    }

    async goTo() {
        await this.page.goto(`${ui()}/extension/jenkins/configurations`)
        await expect(this.page.getByText("Jenkins configurations")).toBeVisible()
        await expect(this.page.getByText("Create config")).toBeVisible()
    }

    async createConfig({name, url, user, password}) {
        await this.page.getByText("Create config").click()
        await this.page.getByLabel("Configuration name").fill(name)
        await this.page.getByLabel("Jenkins URL").fill(url)
        await this.page.getByLabel("Jenkins username").fill(user)
        await this.page.getByLabel("Jenkins password").fill(password)
        await this.page.getByRole("button", {name: "OK"}).click()
    }

    async checkConfigurationCreated(url) {
        await expect(this.page.getByText(url, {exact: true})).toBeVisible()
    }

    async testConfiguration(name) {
        const configRow = this.page.locator(`tr[data-row-key="config-${name}"]`)
        const testButton = configRow.locator('.ot-configuration-test')
        await testButton.click()
        await expect(this.page.locator('.ant-message-success').getByText("Connection OK")).toBeVisible()
    }
}