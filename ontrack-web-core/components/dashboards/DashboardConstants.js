import {gql} from "graphql-request";

export const saveDashboardQuery = gql`
    mutation SaveDashboard(
        $context: String!,
        $contextId: String!,
        $userScope: DashboardContextUserScope!,
        $contextScope: DashboardContextScope!,
        $key: String,
        $name: String!,
        $layoutKey: String!,
        $widgets: [WidgetInstanceInput!]!,
    ) {
        saveDashboard(input: {
            context: $context,
            contextId: $contextId,
            userScope: $userScope,
            contextScope: $contextScope,
            key: $key,
            name: $name,
            layoutKey: $layoutKey,
            widgets: $widgets,
        }) {
            errors {
                message
            }
        }
    }
`
