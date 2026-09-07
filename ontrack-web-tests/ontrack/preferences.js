import {gql} from "graphql-request";
import {graphQLCallMutation} from "@ontrack/graphql";

/**
 * The current user's preferences, which are stored server-side and therefore shared by every
 * spec: the suite signs in as one account, and Playwright runs the files in one worker.
 *
 * A spec which changes a preference — the branch content view, the change log view — has to put
 * it back, or the next spec inherits a page it did not ask for. Reading it from the URL instead
 * is not enough on its own: what a page does with *no* parameter is part of what these specs
 * check.
 */
export const setPreferences = async (ontrack, input) =>
    graphQLCallMutation(
        ontrack.connection,
        'setPreferences',
        gql`
            mutation SetPreferences($input: SetPreferencesInput!) {
                setPreferences(input: $input) {
                    errors {
                        message
                    }
                }
            }
        `,
        {input},
    )

/**
 * Puts the change log page back to the way a user who has never chosen anything sees it — the
 * classic view, and the semantic view's own defaults.
 *
 * The values mirror `Preferences` on the server side; they are repeated here because a spec
 * asserting the default has to be told what it is.
 */
export const resetChangeLogPreferences = async (ontrack) =>
    setPreferences(ontrack, {
        selectedChangeLogViewKey: 'classic',
        changeLogSemanticFormat: 'markdown',
        changeLogSemanticEmojis: true,
        changeLogSemanticIssues: true,
        changeLogSemanticCommits: false,
    })
