export const restCallPost = async (connection, path, body) => {
    const username = connection.credentials.username
    const password = connection.credentials.password

    const token = btoa(`${username}:${password}`)

    return await fetch(
        `${connection.backend}${path}`,
        {
            method: 'POST',
            headers: {
                Authorization: `Basic ${token}`,
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
