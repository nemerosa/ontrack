const config = {};

/**
 * @deprecated Use the `useGraphQLClient` hook.
 */
export default function clientConfig() {
    if (!config.initialized) {
        const url = "http://localhost:8080";
        const username = "admin";
        const password = "admin";

        const token = btoa(`${username}:${password}`);

        config.url = url;
        config.headers = {
            Authorization: `Basic ${token}`
        }
        config.initialized = true;
    }
    return config;
}
