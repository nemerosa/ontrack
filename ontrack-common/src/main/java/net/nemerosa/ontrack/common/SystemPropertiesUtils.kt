package net.nemerosa.ontrack.common

fun String.camelCaseToKebabCase(): String =
    split('.')
        .joinToString(".") { segment ->
            segment.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
        }

fun String.camelCaseToEnvironmentName(): String =
    replace(Regex("[^A-Za-z0-9<>*]"), "_")
        .uppercase()
