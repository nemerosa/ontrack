import {gql} from "graphql-request";
import {gqlProjectContentFragment} from "@components/projects/ProjectGraphQLFragments";

export const gqlEnvironmentData = gql`
    fragment EnvironmentData on Environment {
        id
        name
        description
        order
        tags
    }
`;

export const gqlSlotData = gql`
    fragment SlotData on Slot {
        id
        project {
            ...ProjectContent
        }
        qualifier
        description
        environment {
            id
            name
            order
            image
            tags
        }
    }

    ${gqlProjectContentFragment}
`;

export const gqlSlotPipelineBuildData = gql`
    fragment SlotPipelineBuildData on Build {
        id
        name
        creation {
            time
        }
        branch {
            id
            name
            project {
                id
                name
            }
        }
        promotionRuns(lastPerLevel: true) {
            id
            creation {
                time
            }
            promotionLevel {
                id
                name
                description
                image
                _image
            }
        }
        releaseProperty {
            value
        }
    }
`;

export const gqlSlotPipelineData = gql`
    fragment SlotPipelineData on SlotPipeline {
        id
        number
        start
        end
        status
        finished
        lastChange {
            message
        }
        build {
            ...SlotPipelineBuildData
        }
    }
    ${gqlSlotPipelineBuildData}
`;
