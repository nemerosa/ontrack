import {expect} from "@playwright/test";

export class AutoVersioningTrail {

    constructor(page, section) {
        this.page = page
        this.section = section
    }

    async expectAVTrailVisible({project, branch, visible}) {
        const table = this.avTrailTable()
        const header = table.locator('thead tr th', {hasText: 'Target branch'})
        const headerIndex = await header.evaluate(node => Array.from(node.parentNode.children).indexOf(node))
        const cells = await table.locator(`tbody tr td:nth-child(${headerIndex + 1})`).all()

        let matches = 0
        for (const cell of cells) {
            const projectLink = cell.getByRole('link', {name: project, exact: true})
            const branchLink = cell.getByRole('link', {name: branch, exact: true})
            const hasProject = await projectLink.count() > 0
            const hasBranch = await branchLink.count() > 0
            if (hasProject && hasBranch) {
                matches++
            }
        }

        if (visible) {
            expect(matches, `Expected exactly one cell to have links for project '${project}' and branch '${branch}'`).toBe(1)
        } else {
            expect(matches, `Expected no cell to have links for project '${project}' and branch '${branch}'`).toBe(0)
        }
    }

    async filterByEligibility({onlyEligible}) {
        const onlyEligibleSwitch = this.section.getByRole('switch')
        await expect(onlyEligibleSwitch).toBeVisible()
        await onlyEligibleSwitch.setChecked(onlyEligible)
        await this.filter()
    }

    async filterByProjectName({projectName}) {
        const projectNameInput = this.section.getByPlaceholder('Project name')
        await expect(projectNameInput).toBeVisible()
        await projectNameInput.fill(projectName)
        await this.filter()
    }

    async filter() {
        const filterButton = this.section.getByRole('button', {name: 'Filter', exact: true})
        await filterButton.click()

        await this.page.waitForTimeout(1000)

        const table = this.avTrailTable()
        await expect(table.locator('.ant-spin-spinning')).toBeHidden()
    }

    avTrailTable() {
        return this.section.getByTestId('auto-versioning-trail-table')
    }

}