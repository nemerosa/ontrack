import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

export class JIRAConfigurations {

    constructor(ontrack) {
        this.ontrack = ontrack
    }

    async createConfig({name, url, user, password}) {
        await graphQLCallMutation(
            this.ontrack.connection,
            'createConfiguration',
            gql`
                mutation CreateJiraConfiguration(
                    $name: String!,
                    $data: JSON!,
                ) {
                    createConfiguration(input: {
                        name: $name,
                        type: "jira",
                        data: $data,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                name: name,
                data: {
                    url,
                    user,
                    password,
                }
            }
        )
    }

}