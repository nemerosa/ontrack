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
        return data.createEnvironment.environment
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
