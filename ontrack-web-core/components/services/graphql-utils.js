/**
 * Checking for user errors under a node
 *
 * @deprecated This method collects only the user errors and ignores the GraphQL ones. Use getGraphQLErrors instead.
 */
export const getUserErrors = (node) => {
    if (node.errors && node.errors.length > 0) {
        return node.errors.map(error => error.message);
    } else {
        return null;
    }
}

/**
 * Collects the list of errors from a returned GraphQL call.
 * @param data Raw JSON data returned by the GraphQL call.
 * @param userNodeName Name of the node containing the user data; this function will look for `errors` underneath.
 * @return List of error messages
 */
export const getGraphQLErrors = (data, userNodeName) => {
    const errors = []
    // TODO Getting the errors at top level
    // Errors at user node level
    const userNode = data[userNodeName]
    if (userNode && userNode.errors) {
        userNode.errors.forEach(error => {
            errors.push(error.message)
        })
    }
    // OK
    return errors
}

/**
 * Collects the list of errors from a returned GraphQL call and displays them using the Message API if any.
 *
 * @param data Raw JSON data returned by the GraphQL call.
 * @param userNode Name of the node containing the user data; this function will look for `errors` underneath.
 * @param messageApi Ant Design [message API](https://ant.design/components/message)
 * @return `true` if there were NO error, `false` otherwise.
 */
export const processGraphQLErrors = (data, userNode, messageApi) => {
    const errors = getGraphQLErrors(data, userNode)
    if (errors && errors.length > 0) {
        const message = errors[0].message
        messageApi.error(message)
        return false
    } else {
        return true
    }
}