import {getTable} from "../../support/antd-table-support";
import {BranchLinksTableAutoVersioningCell} from "./BranchLinksTableAutoVersioningCell";

export class BranchLinksTableRow {

    constructor(page, ontrack) {
        this.page = page
        this.ontrack = ontrack
    }

    async expectToBeVisible() {
        this.table = await getTable(this.page, 'branch-links')
        this.row = await this.table.getRowByIndex(1)
    }

    async getAutoVersioningCell() {
        const cell = await this.row.getCell("Auto versioning")
        return new BranchLinksTableAutoVersioningCell(this.page, this.ontrack, cell)
    }

}