import clientConfig from "@client/clientConfig";

export function logout() {
    const config = clientConfig()
    fetch(`${config.url}/logout`, {method: 'POST'}).then(() => {
        location.href = '${config.url}/login?logout';
    });
}