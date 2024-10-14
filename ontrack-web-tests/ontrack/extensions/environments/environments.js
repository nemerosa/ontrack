import {graphQLCall} from "@ontrack/graphql";
import {gql} from "graphql-request";

export class EnvironmentsExtension {
    constructor(ontrack) {
        this.ontrack = ontrack
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
