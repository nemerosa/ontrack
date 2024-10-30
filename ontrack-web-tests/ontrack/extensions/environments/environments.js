import {graphQLCall, graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {generate} from "@ontrack/utils";

export class EnvironmentsExtension {
    constructor(ontrack) {
        this.ontrack = ontrack
    }

    async createEnvironment({name, description, order, tags}) {
        const actualName = name ?? generate('env-')
        const actualDescription = description ?? ''
        const actualOrder = order ?? 1
        const actualTags = tags ?? []
        const data = await graphQLCallMutation(
            this.ontrack.connection,
            'createEnvironment',
            gql`
                mutation CreateEnvironment(
                    $name: String!,
                    $description: String!,
                    $order: Int!,
                    $tags: [String!]!,
                ) {
                    createEnvironment(input: {
                        name: $name,
                        description: $description,
                        order: $order,
                        tags: $tags,
                    }) {
                        errors {
                            message
                        }
                        environment {
                            ...EnvironmentData
                        }
                    }
                }
                ${gqlEnvironmentData}
            `,
            {
                name: actualName,
                description: actualDescription,
                order: actualOrder,
                tags: actualTags,
            }
        )

        const environment = {
            ...data.createEnvironment.environment,
            ontrack: this.ontrack
        }

        environment.createSlot = async ({project}) => {
            return this.createSlot({project, environment})
        }

        return environment
    }

    async findEnvironmentByName(name) {
        const data = await graphQLCall(
            this.ontrack.connection,
            gql`
                query FindEnvironmentByName($name: String!) {
                    environmentByName(name: $name) {
                        ...EnvironmentData
                    }
                }
                ${gqlEnvironmentData}
            `,
            {name}
        )
        return data?.environmentByName
    }

    async createSlot({project, qualifier = "", environment}) {
        const data = await graphQLCallMutation(
            this.ontrack.connection,
            'createSlots',
            gql`
                mutation CreateSlot(
                    $projectId: Int!,
                    $qualifier: String!,
                    $description: String!,
                    $environmentId: String!,
                ) {
                    createSlots(input: {
                        projectId: $projectId,
                        qualifier: $qualifier,
                        description: $description,
                        environmentIds: [$environmentId]
                    }) {
                        slots {
                            slots {
                                ...SlotData
                            }
                        }
                        errors {
                            message
                        }
                    }
                }
                ${gqlSlotData}
            `,
            {
                projectId: project.id,
                qualifier: qualifier,
                description: "",
                environmentId: environment.id,
            }
        )
        const slot = data.createSlots.slots.slots[0]
        slot.ontrack = this.ontrack
        slot.createPipeline = async ({build}) => {
            return await this.createPipeline({slot, build});
        }
        return slot
    }

    async createPipeline({slot, build}) {
        const data = await graphQLCallMutation(
            this.ontrack.connection,
            'startSlotPipeline',
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
                            ...PipelineData
                        }
                        errors {
                            message
                        }
                    }
                }
                ${gqlPipelineData}
            `,
            {
                slotId: slot.id,
                buildId: build.id,
            }
        )

        const pipeline = data.startSlotPipeline.pipeline
        pipeline.ontrack = this.ontrack
        return pipeline
    }

    async addAdmissionRule({slot, description = "", ruleId, ruleConfig}) {
        await graphQLCallMutation(
            this.ontrack.connection,
            'saveSlotAdmissionRuleConfig',
            gql`
                mutation CreateAdmissionRule(
                    $slotId: String!,
                    $description: String!,
                    $ruleId: String!,
                    $ruleConfig: JSON!,
                ) {
                    saveSlotAdmissionRuleConfig(input: {
                        slotId: $slotId,
                        description: $description,
                        ruleId: $ruleId,
                        ruleConfig: $ruleConfig,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                slotId: slot.id,
                description,
                ruleId,
                ruleConfig,
            }
        )
    }

    async addManualApproval({slot}) {
        await this.addAdmissionRule({
            slot,
            ruleId: "manual",
            ruleConfig: {
                message: "Approval message",
            }
        })
    }
}

const gqlEnvironmentData = gql`
    fragment EnvironmentData on Environment {
        id
        name
        description
        order
        tags
    }
`

const gqlSlotData = gql`
    fragment SlotData on Slot {
        id
        description
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
`

const gqlPipelineData = gql`
    fragment PipelineData on SlotPipeline {
        id
        number
        slot {
            ...SlotData
        }
    }
    ${gqlSlotData}
`
