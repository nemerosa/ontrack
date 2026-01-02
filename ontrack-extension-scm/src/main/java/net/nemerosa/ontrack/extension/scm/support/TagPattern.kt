package net.nemerosa.ontrack.extension.scm.support

import java.util.regex.Pattern

class TagPattern(val pattern: String) {

    fun clone(replacementFunction: (String) -> String) = TagPattern(replacementFunction(pattern))

    fun isValidTagName(name: String): Boolean {
        return pattern.isBlank() || createRegex().matcher(name).matches()
    }

    fun getBuildNameFromTagName(tagName: String): String? {
        if (pattern.isBlank()) {
            return tagName
        } else {
            val matcher = createRegex().matcher(tagName)
            return if (matcher.matches()) {
                if (matcher.groupCount() > 0) {
                    matcher.group(1)
                } else {
                    matcher.group(0)
                }
            } else {
                null
            }
        }
    }

    fun getTagNameFromBuildName(buildName: String): String? {
        if (pattern.isBlank()) {
            return buildName
        } else {
            // Extraction of the build pattern, if any
            val buildPartRegex = "\\((.*\\*/*)\\)"
            val buildPartPattern = Pattern.compile(buildPartRegex)
            val buildPartMatcher = buildPartPattern.matcher(pattern)
            if (buildPartMatcher.find()) {
                val buildPart = buildPartMatcher.group(1)
                if (Pattern.matches(buildPart, buildName)) {
                    val tag = StringBuffer()
                    do {
                        buildPartMatcher.appendReplacement(tag, buildName)
                    } while (buildPartMatcher.find())
                    buildPartMatcher.appendTail(tag)
                    return tag.toString()
                } else {
                    return null
                }
            } else if (createRegex().matcher(buildName).matches()) {
                return buildName
            } else {
                return null
            }
        }
    }

    private fun createRegex(): Pattern {
        return Pattern.compile(pattern.replace("*", ".*"))
    }
}
