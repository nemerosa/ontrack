package net.nemerosa.ontrack.common

/**
 * Utility method to replace a regex group by a value.
 *
 * @receiver The regex to use
 * @param input Input string to match against this regex
 * @param group Index of the matching group
 * @param replacement String to use as a replacement of the group
 * @return Replaced string, or the initial [input] if there is no match
 */
fun Regex.replaceGroup(input: String, group: Int, replacement: String): String {
    return matchEntire(input)
        ?.run {
            groups[group]
        }
        ?.run {
            val builder = StringBuilder()
            builder.append(input.substring(0 until range.first))
            builder.append(replacement)
            builder.append(input.substring(range.last + 1))
            builder.toString()
        }
        ?: input
}
