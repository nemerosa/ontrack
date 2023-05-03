const config = {};

export default function clientConfig() {
    if (!config.initialized) {
        if (process.env.NEXT_PUBLIC_LOCAL === 'true') {
            const url = "http://localhost:8080";
            const username = "admin";
            const password = "admin";

            const token = btoa(`${username}:${password}`);

            config.url = url;
            config.headers = {
                Authorization: `Basic ${token}`
            };
        } else {
            config.url = '';
            config.headers = {};
        }
    }
    config.initialized = true;
    return config;
}
