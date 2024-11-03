import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {ontrack} from "@ontrack/ontrack";

export const createPipeline = async ({project, slot}) => {
    const branch = await project.createBranch("main")
    const build = await branch.createBuild()

    const data = await graphQLCallMutation(
        ontrack().connection,
        '',
        gql`
            mutation CreatePipeline(
                $slotId: String!,
                $buildId: Int!,
            ) {
                startSlotPipeline(input: {
                    slotId: $slotId,
                    buildId: $buildId,
                }) {
                    pipeline {
                        id
                        number
                        slot {
                            id
                            project {
                                id
                                name
                            }
                            qualifier
                            environment {
                                id
                                name
                            }
                        }
                    }
                    errors {
                        message
                    }
                }
            }
        `,
        {
            slotId: slot.id,
            buildId: build.id,
        }
    )

    return {
        pipeline: data.startSlotPipeline.pipeline
    }
}