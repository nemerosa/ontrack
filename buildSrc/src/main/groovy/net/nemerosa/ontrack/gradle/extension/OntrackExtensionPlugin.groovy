package net.nemerosa.ontrack.gradle.extension

import com.liferay.gradle.plugins.node.tasks.ExecuteNodeScriptTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class OntrackExtensionPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "[ontrack] Applying INTERNAL Ontrack plugin to ${project.path}"

        /**
         * Project's configuration
         */

        project.extensions.create('ontrack', OntrackExtension)

        /**
         * Node plugin setup
         */

        project.apply plugin: 'com.liferay.node'
        project.node {
            global = true
            nodeVersion = '8.10.0'
            npmVersion = '5.7.1'
            download = true
        }

        /**
         * Project configuration
         */

        project.ext {
            ontrackWebDir = project.file("${project.projectDir}/.gradle/ontrack")
            ontrackWebNodeModulesDir = new File(project.projectDir, "node_modules")
        }

        /**
         * NPM tasks
         */

        project.tasks.create('copyPackageJson') {
            doLast {
                println "[ontrack] Copies the package.json file to ${project.projectDir}"
                project.file('package.json').text = getClass().getResourceAsStream('/extension/package.json').text
            }
        }

        project.tasks.create('copyGulpFile') {
            doLast {
                println "[ontrack] Copies the gulpfile.js file to ${project.projectDir}"
                project.file('gulpfile.js').text = getClass().getResourceAsStream('/extension/gulpfile.js').text
            }
        }

        project.tasks.npmInstall {
            dependsOn project.tasks.copyPackageJson

            nodeModulesCacheDir = "${project.rootDir}/.gradle/node_modules_cache"
            workingDir = project.projectDir

            inputs.file project.file('package.json')
            outputs.dir project.file('node_modules')
        }

        /**
         * Gulp call
         */

        project.tasks.create('web', ExecuteNodeScriptTask) {
            dependsOn 'npmInstall'
            dependsOn 'copyGulpFile'

            inputs.dir project.file('src/main/resources/static')
            outputs.file project.file('build/web/dist/module.js')

            doFirst {
                project.mkdir project.buildDir
                println "[ontrack] Generating web resources of ${project.extensions.ontrack.id(project)} in ${project.buildDir}"
            }

            workingDir = project.projectDir
            scriptFile = project.file('node_modules/gulp/bin/gulp')
            args = [
                    'default',
                    '--extension', project.extensions.ontrack.id(project),
                    '--version', project.version,
                    '--src', project.file('src/main/resources/static'),
                    '--target', project.buildDir
            ]
        }

        /**
         * Version file
         */

        project.tasks.create('ontrackProperties') {
            description "Prepares the ontrack META-INF file"
            doLast {
                project.mkdir("build")
                project.file("build/ontrack.properties").text = """\
# Ontrack extension properties
version = ${project.version}
"""
            }
        }

        /**
         * Update of the JAR task
         */

        project.tasks.jar {
            dependsOn 'web'
            dependsOn 'ontrackProperties'
            from('build/web/dist') {
                into { "static/extension/${project.extensions.ontrack.id(project)}/${project.version}/" }
            }
            from('build') {
                include 'ontrack.properties'
                into "META-INF/ontrack/extension/"
                rename { "${project.extensions.ontrack.id(project)}.properties" }
            }
            exclude 'static/**/*.js'
            exclude 'static/**/*.html'
        }

    }

}
