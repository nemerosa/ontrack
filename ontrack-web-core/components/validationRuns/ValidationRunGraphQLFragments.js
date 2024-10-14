import {gql} from "graphql-request";

export const gqlValidationRunContent = gql`
    fragment ValidationRunContent on ValidationRun {
        id
        runOrder
        data {
            descriptor {
                id
                feature {
                    id
                }
            }
            data
        }
        authorizations {
            name
            action
            authorized
        }
        runInfo {
            sourceType
            sourceUri
            triggerType
            triggerData
            runTime
        }
        lastStatus {
            statusID {
                id
                name
            }
        }
        validationRunStatuses {
            id
            creation {
                user
                time
            }
            description
            annotatedDescription
            statusID {
                id
                name
            }
            authorizations {
                name
                action
                authorized
            }
        }
    }
`
