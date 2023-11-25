/**
 * Name of the cookie which contains the encrypted Ontrack token used for the connection to Ontrack.
 * @type {string}
 */
export const cookieName = 'ontrack'

/**
 * Default validity (in ms) for the cookie
 * @type {number}
 */
const cookieMs = 30 * 60 * 1000 // 30 minutes

/**
 * Default options for the session cookie
 */
export const cookieOptions = () => {
    return {
        path: '/',
        expires: new Date(Date.now() + cookieMs),
    }
}

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
    return process.env.NEXT_PUBLIC_ONTRACK_URL ?? defaultOntrackUrl
}


export const ontrackUiUrl = () => {
    return process.env.NEXT_PUBLIC_ONTRACK_UI_URL ?? defaultOntrackUI
}

export const isConnectionLoggingEnabled = () => {
    return process.env.NEXT_PUBLIC_ONTRACK_CONNECTION_LOGGING === 'true'
}
