import {gql} from "graphql-request";

export const gqlNotificationRecordContent = gql`
    fragment NotificationRecordContent on NotificationRecord {
        key: id
        id
        source {
            id
            data
        }
        channel
        channelConfig
        event
        result {
            type
            message
            output
        }
        timestamp
    }
`
