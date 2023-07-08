import {gql} from "graphql-request";

export const saveDashboardQuery = gql`
    mutation SaveDashboard(
        $uuid: String,
        $name: String!,
        $userScope: DashboardContextUserScope!,
        $layoutKey: String!,
        $widgets: [WidgetInstanceInput!]!,
    ) {
        saveDashboard(input: {
            uuid: $uuid,
            name: $name,
            userScope: $userScope,
            layoutKey: $layoutKey,
            widgets: $widgets,
            select: true,
        }) {
            errors {
                message
            }
            dashboard {
                uuid
                name
                userScope
                layoutKey
                widgets {
                    uuid
                    key
                    config
                }
            }
        }
    }
`
