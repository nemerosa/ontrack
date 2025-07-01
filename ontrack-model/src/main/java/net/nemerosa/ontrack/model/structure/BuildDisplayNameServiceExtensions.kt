package net.nemerosa.ontrack.model.structure

fun BuildDisplayNameService.getBuildDisplayNameOrName(build: Build) =
    getFirstBuildDisplayName(build) ?: build.name
