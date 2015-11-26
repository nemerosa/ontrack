package net.nemerosa.ontrack.extension.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class OntrackExtensionPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "[ontrack] Applying the `ontrack` plugin to ${project.path}"
    }
}
