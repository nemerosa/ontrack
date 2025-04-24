export const ui = () => {
    return process.env.ONTRACK_UI_URL ?? "http://localhost:3000"
}

export const backend = () => {
    return process.env.ONTRACK_BACKEND_URL ?? "http://localhost:8080"
}

export const credentials = () => {
    return {
        username: process.env.ONTRACK_CREDENTIALS_USERNAME ?? "admin",
        password: process.env.ONTRACK_CREDENTIALS_PASSWORD ?? "admin",
    }
}
