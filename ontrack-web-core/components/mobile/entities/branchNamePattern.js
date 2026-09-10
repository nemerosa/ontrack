/**
 * What a phone user types into the branch filter, turned into what the server
 * expects.
 *
 * `Project.branches(name:)` is **a regular expression**, not the `ILIKE` the
 * project list's `projects(pattern:)` uses: the repository matches it with
 * Postgres' `~`, which is unanchored and case-sensitive. Handing it the raw text
 * would be wrong twice over - `release/1.0` would match `release/1x0`, and a
 * lone `(` would not narrow the list but fail the whole query.
 *
 * So the typed text is escaped to a literal and made case-insensitive with an
 * embedded `(?i)`, which Postgres' advanced regular expressions support. The
 * result is a case-insensitive substring match: the same thing the project
 * list's filter does, which is what a user moving between the two screens
 * expects.
 *
 * A pure module - no React - so the escaping can be tested on its own.
 */

/**
 * Every character that means something in a POSIX regular expression, and
 * therefore has to be escaped to stand for itself.
 */
const METACHARACTERS = /[\\^$.|?*+()[\]{}]/g

/**
 * The `name` argument for `Project.branches`.
 *
 * @param {string} [text] What the user typed.
 * @returns {string|null} The pattern, or `null` when nothing was typed - the
 *   screen must send `null` rather than `''`, because a blank pattern is not a
 *   filter and the screen must not head the whole list as if it were one.
 */
export function branchNamePattern(text) {
    const trimmed = typeof text === 'string' ? text.trim() : ''
    if (!trimmed) return null
    return `(?i)${trimmed.replace(METACHARACTERS, '\\$&')}`
}
