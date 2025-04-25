import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

export const admin = (ontrack) => {
    const admin = {
        ontrack,
    }

    admin.revokeToken = async (tokenName) => revokeToken(admin, tokenName)

    return admin
}

const revokeToken = async (admin, tokenName) => {
    await graphQLCallMutation(
        admin.ontrack.connection,
        'revokeToken',
        gql`
            mutation RevokeToken($name: String!) {
                revokeToken(input: {name: $name}) {
                    errors {
                        message
                    }
                }
            }
        `,
        {name: tokenName}
    )
}
