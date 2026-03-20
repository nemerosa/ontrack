import {Table, TableRow} from "../../support/antd-table-support";
import {NotificationDetails} from "./NotificationDetails";

export class NotificationsTable {

    constructor(page, table) {
        this.page = page
        this.table = table
    }

    async displayFirstNotificationForChannel(channel) {
        const tableHelper = new Table(this.page, this.table)

        const row = await tableHelper.findRow(async row => {
            const tableRow = new TableRow(this.page, tableHelper, row)
            const cell = await tableRow.getCell("Channel")
            const cellContent = await cell.textContent()
            return cellContent === channel
        })

        if (row) {
            const details = await row.expand()
            return new NotificationDetails(this.page, details)
        } else {
            throw new Error(`No row found for channel ${channel}`)
        }
    }

}