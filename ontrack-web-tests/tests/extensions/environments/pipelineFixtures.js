import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {ontrack} from "@ontrack/ontrack";

export const createPipeline = async ({
                                         project,
                                         slot,
                                         forceDone = false,
                                         forceDoneMessage = '',
                                         branchSetup,
                                     }) => {
    const branch = await project.createBranch("main")

    if (branchSetup) {
        await branchSetup(branch)
    }

    const build = await branch.createBuild()

    const data = await graphQLCallMutation(
        ontrack().connection,
        '',
        gql`
            mutation CreatePipeline(
                $slotId: String!,
                $buildId: Int!,
                $forceDone: Boolean!,
                $forceDoneMessage: String,
            ) {
                startSlotPipeline(input: {
                    slotId: $slotId,
                    buildId: $buildId,
                    forceDone: $forceDone,
                    forceDoneMessage: $forceDoneMessage,
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
            forceDone,
            forceDoneMessage,
        }
    )

    return {
        pipeline: data.startSlotPipeline.pipeline
    }
}