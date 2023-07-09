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
                authorizations {
                    edit
                    share
                    delete
                }
                widgets {
                    uuid
                    key
                    config
                }
            }
        }
    }
`

export const shareDashboardQuery = gql`
    mutation ShareDashboard($uuid: String!) {
        shareDashboard(input: {uuid: $uuid}) {
            errors {
                message
            }
            dashboard {
                uuid
                name
                userScope
                layoutKey
                authorizations {
                    edit
                    share
                    delete
                }
                widgets {
                    uuid
                    key
                    config
                }
            }
        }
    }
`
export const deleteDashboardQuery = gql`
    mutation DeleteDashboard($uuid: String!) {
        deleteDashboard(input: {uuid: $uuid}) {
            errors {
                message
            }
        }
    }
`

export const loadDashboardsQuery = gql`
    query LoadDashboards {
        userDashboards {
            ...DashboardContent
        }
        userDashboard {
            ...DashboardContent
        }
    }

    fragment DashboardContent on Dashboard {
        uuid
        name
        userScope
        layoutKey
        authorizations {
            edit
            share
            delete
        }
        widgets {
            uuid
            key
            config
        }
    }
`

export const selectDashboardQuery = gql`
    mutation SelectDashboard($uuid: String!) {
        selectDashboard(input: {uuid: $uuid}) {
            errors {
                message
            }
        }
    }
`
