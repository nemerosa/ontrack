package net.nemerosa.ontrack.gradle.extension

import com.moowork.gradle.node.task.NpmTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class OntrackExtensionPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "[ontrack] Applying Ontrack plugin to ${project.path}"

        /**
         * NPM setup
         */
        project.apply plugin: 'com.moowork.node'
        project.node {
            version = '4.2.2'
            npmVersion = '4.2.2'
            download = true
        }

        /**
         * NPM tasks
         */

        project.ext {
            cacheDir = project.gradle.getGradleUserHomeDir() as String ?: "${System.getProperty("user.home")}/.cache/gradle"
        }

        project.tasks.create('npmCacheConfig', NpmTask) {
            description "Configure the NPM cache"
            def npmCacheDir = "${project.ext.cacheDir}/caches/npm"
            outputs.files project.file(npmCacheDir)
            args = [ 'config', 'set', 'cache', npmCacheDir ]
        }

        project.tasks.create('npmPackageFile') {
            description "Copies the package.json in the build directory of the extension module"
            doLast {
                def source = getClass().getResourceAsStream('/extension/package.json')
                project.mkdir 'build'
                project.file('build/package.json').text = source.text
            }
        }

        project.tasks.create('npmPackages', NpmTask) {
            dependsOn project.tasks.npmCacheConfig
            dependsOn project.tasks.npmPackageFile
            description "Install Node.js packages"
            workingDir = project.file('build')
            args = [ 'install' ]
            inputs.files project.file('build/package.json')
            outputs.files project.file('build/node_modules')
        }
    }

}
