import {graphQLCall} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {AutoVersioningAuditEntry} from "@ontrack/extensions/auto-versioning/AutoVersioningAuditEntry";
import {AutoVersioningAuditOrder} from "@ontrack/extensions/auto-versioning/AutoVersioningAuditOrder";
import {AutoVersioningAuditState} from "@ontrack/extensions/auto-versioning/AutoVersioningAuditState";

export class AutoVersioningAuditMgt {
    constructor(ontrack) {
        this.ontrack = ontrack
    }

    async entries({source, project, branch, version}) {
        const data = await graphQLCall(
            this.ontrack.connection,
            gql`
                query AutoVersioningAuditEntries(
                    $source: String,
                    $project: String,
                    $branch: String,
                    $version: String,
                ) {
                    autoVersioningAuditEntries(filter: {
                        source: $source,
                        project: $project,
                        branch: $branch,
                        version: $version,
                    }) {
                        pageItems {
                            order {
                                uuid
                            }
                            mostRecentState {
                                state
                            }
                        }
                    }
                }
            `,
            {
                source,
                project,
                branch,
                version,
            }
        )
        return data.autoVersioningAuditEntries.pageItems.map(entry => {
            return new AutoVersioningAuditEntry({
                order: new AutoVersioningAuditOrder(entry.order),
                mostRecentState: new AutoVersioningAuditState(entry.mostRecentState),
            })
        })
    }
}