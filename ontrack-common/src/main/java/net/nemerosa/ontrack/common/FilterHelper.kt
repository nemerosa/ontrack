package net.nemerosa.ontrack.common

object FilterHelper {

    fun includes(
        text: String,
        includes: List<String>,
        excludes: List<String> = emptyList(),
        regexCreation: (pattern: String) -> Regex = { it.toRegex(RegexOption.IGNORE_CASE) },
    ): Boolean {
        val including = includes.any {
            val regex = regexCreation(it)
            regex.matches(text)
        }
        if (including) {
            if (excludes.isNotEmpty()) {
                val excluding = excludes.any {
                    val regex = regexCreation(it)
                    regex.matches(text)
                }
                return !excluding
            } else {
                return true
            }
        } else {
            return false
        }
    }

}