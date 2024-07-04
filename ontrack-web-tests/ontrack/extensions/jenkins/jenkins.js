import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

export class JenkinsConfigurations {

    constructor(ontrack) {
        this.ontrack = ontrack
    }

    async createConfig({name, url, user, password}) {
        await graphQLCallMutation(
            this.ontrack.connection,
            'createConfiguration',
            gql`
                mutation CreateJenkinsConfiguration(
                    $name: String!,
                    $data: JSON!,
                ) {
                    createConfiguration(input: {
                        name: $name,
                        type: "jenkins",
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