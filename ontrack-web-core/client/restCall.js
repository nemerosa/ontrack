import clientConfig from "@client/clientConfig";

const config = clientConfig()

export default async function restCall(uri) {
    const response = await fetch(
        `${config.url}${uri}`,
        {
            headers: config.headers,
        }
    )
    return response.json()
};
