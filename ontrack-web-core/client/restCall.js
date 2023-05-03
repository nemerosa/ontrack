import clientConfig from "@client/clientConfig";

const config = clientConfig()

export default async function restCall(uri) {
    return await fetch(
        `${config.url}${uri}`,
        {
            headers: config.headers,
        }
    )
};
