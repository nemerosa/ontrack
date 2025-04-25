export const restCallPost = async (connection, path, body) => {
    const token = connection.token
    if (!token) {
        throw new Error("No token is available in the connection.")
    }
    return await fetch(
        `${connection.backend}${path}`,
        {
            method: 'POST',
            headers: {
                'X-Ontrack-Token': token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        }
    )
}

export const restCallPostForJson = async (connection, path, body) => {
    const response = await restCallPost(connection, path, body)
    return response.json()
}
