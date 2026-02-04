import {expect} from "@playwright/test";

export class Table {

    constructor(page, table) {
        this.page = page
        this.table = table
    }

    async getRow(cellId) {
        const row = this.table.getByRole('row', {name: cellId, exact: false})
        await expect(row).toBeVisible()

        return new TableRow(this.page, this, row)
    }

    async getRowByIndex(index) {
        const row = this.table.getByRole('row').nth(index)
        await expect(row).toBeVisible()

        return new TableRow(this.page, this, row)
    }

    async getColumnIndex(columnName) {
        const nameColumnIndex = await this.table.locator('thead tr th').allTextContents()
        const index = nameColumnIndex.indexOf(columnName)
        if (index < 0) {
            throw Error(`Could not find column with title ${columnName}`)
        }
        return index
    }

}

export class TableRow {
    constructor(page, table, row) {
        this.page = page
        this.table = table
        this.row = row
    }

    async getCell(columnName) {
        const index = await this.table.getColumnIndex(columnName)
        return this.row.getByRole('cell').nth(index)
    }
}

export async function getTable(page, id) {
    const table = page.getByTestId(id)
    await expect(table).toBeVisible()
    return new Table(page, table)
}
