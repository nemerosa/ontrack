/**
 * The four options of the semantic change log view: what they are called in the URL, what
 * they are called in the user preferences, and what they are when nobody has chosen.
 *
 * The parameter names are flat (`?format=`, `?emojis=`, ...) because only this page reads
 * them and none of them clashes with `from`, `to`, `fromBranch`, `toBranch` or `project`.
 * They are public the moment someone shares a link, so they are effectively permanent — see
 * `docs/adr/0008-change-log-views.md`.
 *
 * The defaults are the ones `Preferences` carries on the server side; they are repeated here
 * because the page must read the same way before the preferences have loaded.
 */
export const semanticChangeLogOptions = [
    {
        name: 'format',
        param: 'format',
        preference: 'changeLogSemanticFormat',
        // The syntax of the places this text gets pasted: release notes, PR descriptions, chat
        defaultValue: 'markdown',
        type: 'string',
    },
    {
        name: 'emojis',
        param: 'emojis',
        preference: 'changeLogSemanticEmojis',
        defaultValue: true,
        type: 'boolean',
    },
    {
        name: 'issues',
        param: 'issues',
        preference: 'changeLogSemanticIssues',
        // The semantic view has no issues panel of its own, so this is the only place its
        // issues appear
        defaultValue: true,
        type: 'boolean',
    },
    {
        name: 'commits',
        param: 'commits',
        preference: 'changeLogSemanticCommits',
        // The commits cell is the classic view's answer to the same question
        defaultValue: false,
        type: 'boolean',
    },
]

/**
 * Reads an option out of a URL parameter.
 *
 * Anything a URL cannot be trusted to carry is ignored rather than guessed at: an `?emojis=`
 * which is neither `true` nor `false` leaves the preference in charge, and an empty `?format=`
 * does the same.
 *
 * @param option Option being read
 * @param value Raw value of the query parameter, if any
 * @returns The value, or `undefined` when the parameter says nothing usable
 */
export function parseSemanticOptionParam(option, value) {
    if (value === undefined || value === null || value === '') return undefined
    if (option.type === 'boolean') {
        if (value === 'true') return true
        if (value === 'false') return false
        return undefined
    }
    return value
}

/**
 * Renders an option for the URL. Booleans go in as `true`/`false` rather than being dropped
 * when false: the parameter is what makes a shared link reproduce the sender's reading, so it
 * has to say "off" as explicitly as it says "on".
 */
export function formatSemanticOptionParam(value) {
    return typeof value === 'boolean' ? String(value) : value
}
