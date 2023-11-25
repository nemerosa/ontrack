/**
 * Checking for user errors under a node
 */
export const getUserErrors = (node) => {
    if (node.errors && node.errors.length > 0) {
        return node.errors.map(error => error.message);
    } else {
        return null;
    }
}
