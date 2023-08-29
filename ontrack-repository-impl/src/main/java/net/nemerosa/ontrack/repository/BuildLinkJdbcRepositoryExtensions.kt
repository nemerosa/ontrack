package net.nemerosa.ontrack.repository


fun expandBuildPattern(buildPattern: String?): String = if (buildPattern.isNullOrBlank()) {
    "%"
} else {
    buildPattern.replace("*", "%")
}
