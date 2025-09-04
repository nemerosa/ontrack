import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

export class AbstractSettings {

    constructor(ontrack) {
        this.ontrack = ontrack
    }

    async doSaveSettings({id, values}) {
        return await graphQLCallMutation(
            this.ontrack.connection,
            'saveSettings',
            gql`
                mutation SaveSettings(
                    $id: String!,
                    $values: JSON!,
                ) {
                    saveSettings(input: {
                        id: $id,
                        values: $values,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                id,
                values,
            }
        )
    }
}