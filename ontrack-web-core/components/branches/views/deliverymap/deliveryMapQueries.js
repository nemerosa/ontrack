import {gql} from "graphql-request";

/**
 * The GraphQL the delivery map content view runs.
 *
 * One document: the map is one thing the server assembles, and splitting it would only mean
 * assembling it again on this side.
 *
 * `data` is fetched as opaque JSON on purpose. Checkpoint kinds are open - an extension contributes
 * one the core has never heard of - so a typed field per kind would mean this query changing every
 * time a new kind appears, and a kind's payload is only ever read by the component registered for
 * that kind.
 */
const gqlDeliveryMapCheckpoint = gql`
    fragment DeliveryMapCheckpointContent on DeliveryMapCheckpoint {
        id
        type
        name
        description
        data
        arrival {
            build {
                id
                name
                displayName
                releaseProperty {
                    value
                }
            }
            time
            status {
                id
                name
                passed
            }
            lag
        }
    }
`

export const gqlDeliveryMap = gql`
    query DeliveryMap($branchId: Int!) {
        branch(id: $branchId) {
            id
            deliveryMap {
                checkpoints {
                    ...DeliveryMapCheckpointContent
                    members {
                        ...DeliveryMapCheckpointContent
                    }
                }
                edges {
                    id
                    kind
                    source
                    target
                }
                head {
                    id
                    name
                    displayName
                    releaseProperty {
                        value
                    }
                    creation {
                        time
                    }
                }
            }
        }
    }
    ${gqlDeliveryMapCheckpoint}
`
