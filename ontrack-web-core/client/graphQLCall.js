import {GraphQLClient} from "graphql-request";
import clientConfig from "@client/clientConfig";

const config = clientConfig()

const graphQLClient = new GraphQLClient(
    `${config.url}/graphql`, {
        headers: config.headers,
    });

/**
 * Checking for user errors under a node
 */
export const getUserErrors = (node) => {
    if (node.errors && node.errors.length > 0) {
        return node.errors.map(error => error.message);
    } else {
        return null;
    }
};

export default async function graphQLCall(query, variables) {
    return await graphQLClient.request(query, variables);
};
