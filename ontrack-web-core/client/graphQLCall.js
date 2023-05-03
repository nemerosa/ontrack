import {GraphQLClient} from "graphql-request";

let graphQLClient;

if (process.env.NEXT_PUBLIC_LOCAL === 'true') {
    const graphQLUrl = "http://localhost:8080/graphql";
    const graphQLUsername = "admin";
    const graphQLPassword = "admin";

    const graphQLToken = btoa(`${graphQLUsername}:${graphQLPassword}`);

    graphQLClient = new GraphQLClient(graphQLUrl, {
        headers: {
            Authorization: `Basic ${graphQLToken}`
        }
    });
} else {
    graphQLClient = new GraphQLClient("/graphql", {});
}

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
