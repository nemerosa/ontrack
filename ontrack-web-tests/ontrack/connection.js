import {mgtAccountToken} from "@ontrack/mgt";

export class Credentials {
    constructor({username, password}) {
        this.username = username
        this.password = password
    }
}

export class Connection {

    constructor({ui, backend, mgt, credentials, token}) {
        this.ui = ui
        this.backend = backend
        this.mgt = mgt
        this.credentials = credentials
        this.token = token
    }

    withToken(token) {
        return new Connection({
            ...this,
            token
        })
    }

}

export const createConnection = async () => {
    const mgtUrl = process.env.ONTRACK_MGT_URL ?? "http://localhost:8800"
    const username = process.env.ONTRACK_CREDENTIALS_USERNAME ?? "admin@ontrack.local"
    return new Connection({
        ui: process.env.ONTRACK_UI_URL ?? "http://localhost:3000",
        backend: process.env.ONTRACK_BACKEND_URL ?? "http://localhost:8080",
        mgt: mgtUrl,
        credentials: new Credentials({
            username,
            password: process.env.ONTRACK_CREDENTIALS_PASSWORD ?? "admin",
        }),
        token: await mgtAccountToken(username, mgtUrl)
    })
}
