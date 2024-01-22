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
}