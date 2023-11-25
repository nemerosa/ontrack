/**
 * Name of the cookie which contains the encrypted Ontrack token used for the connection to Ontrack.
 * @type {string}
 */
export const cookieName = 'ontrack'

/**
 * URL of the Ontrack backend if not specified.
 * @type {string}
 */
const defaultOntrackUrl = "http://localhost:8080"

/**
 * URL of the Ontrack Next UI if not specified.
 * @type {string}
 */
const defaultOntrackUI = "http://localhost:3000"

export const ontrackUrl = () => {
    return process.env.ONTRACK_URL ?? defaultOntrackUrl
}


export const ontrackUiUrl = () => {
    return process.env.ONTRACK_UI_URL ?? defaultOntrackUI
}

export const isConnectionLoggingEnabled = () => {
    return process.env.ONTRACK_CONNECTION_LOGGING === 'true'
}