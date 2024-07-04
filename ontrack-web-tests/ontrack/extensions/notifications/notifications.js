import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

export const registerNotificationExtensions = (projectEntity) => {
    projectEntity.subscribe = async ({events, keywords, channel, channelConfig, contentTemplate}) => {
        await graphQLCallMutation(
            projectEntity.ontrack.connection,
            'subscribeToEvents',
            gql`
                mutation CreateSubscription(
                    $entityType: ProjectEntityType!,
                    $entityId: Int!,
                    $events: [String!]!,
                    $keywords: String,
                    $channel: String!,
                    $channelConfig: JSON!,
                    $contentTemplate: String,
                ) {
                    subscribeToEvents(input: {
                        projectEntity: {
                            type: $entityType,
                            id: $entityId,
                        },
                        events: $events,
                        keywords: $keywords,
                        channel: $channel,
                        channelConfig: $channelConfig,
                        contentTemplate: $contentTemplate,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                entityType: projectEntity.type,
                entityId: projectEntity.id,
                events,
                keywords,
                channel,
                channelConfig,
                contentTemplate,
            }
        )
    }
}