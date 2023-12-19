import {generate} from "@ontrack/utils";
import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";

export const admin = (ontrack) => {
    const admin = {
        ontrack,
    }

    admin.revokeToken = async (tokenName) => revokeToken(admin, tokenName)
    admin.createAccount = async () => createAccount(admin)

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

const createAccount = async (admin) => {
    const username = generate('usr_')
    const password = generate('psw_')

    await graphQLCallMutation(
        admin.ontrack.connection,
        'createBuiltInAccount',
        gql`
            mutation CreateAccount(
                $username: String!,
                $email: String!,
                $fullName: String!,
                $password: String!,
            ) {
                createBuiltInAccount(input: {
                    name: $username,
                    email: $email,
                    fullName: $fullName,
                    password: $password,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            username: username,
            email: `${username}@ontrack.run`,
            fullName: `${username} Ontrack`,
            password: password,
        }
    )

    return {
        username,
        password,
    }
}