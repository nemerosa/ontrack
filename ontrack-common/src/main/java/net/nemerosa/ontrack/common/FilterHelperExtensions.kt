package net.nemerosa.ontrack.common

fun includes(text: String, includes: String) =
    FilterHelper.includes(text, listOf(includes))

fun includes(text: String, includes: String, excludes: String) =
    FilterHelper.includes(text, listOf(includes), listOf(excludes))

fun excludes(text: String, includes: String, excludes: String) =
    !includes(text, includes, excludes)
