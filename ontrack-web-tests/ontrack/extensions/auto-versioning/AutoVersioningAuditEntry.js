export class AutoVersioningAuditEntry {
    constructor({order, mostRecentState}) {
        this.order = order
        this.mostRecentState = mostRecentState
    }
}