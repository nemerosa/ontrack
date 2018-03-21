package net.nemerosa.ontrack.gradle.extension

import com.moowork.gradle.node.task.NodeTask
import com.moowork.gradle.node.task.NpmTask
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
         * NPM setup
         */

        project.apply plugin: 'com.moowork.node'
        project.node {
            version = '4.2.2'
            npmVersion = '4.2.2'
            download = true
            workDir = project.file("${project.projectDir}/.gradle/nodejs")
        }

        /**
         * NPM tasks
         */

        project.ext {
            if (project.hasProperty('node.gradle.cache')) {
                cacheDir = project.properties['node.gradle.cache']
            } else {
                cacheDir = project.gradle.getGradleUserHomeDir() as String ?: "${System.getProperty("user.home")}/.cache/gradle"
            }
            npmCacheDir = "${cacheDir}/caches/npm"
            ontrackCacheDir = "${cacheDir}/caches/ontrack/extension"
        }

        project.tasks.create('npmCacheConfig', NpmTask) {
            doFirst {
                println "[ontrack] Configure the NPM cache in ${project.npmCacheDir}"
            }
            args = [ 'config', 'set', 'cache', project.npmCacheDir ]
            outputs.dir project.file(project.npmCacheDir)
        }

        project.tasks.create('copyPackageJson') {
            // Without any input reference, the target file will never be replaced
            // outputs.file new File(project.ontrackCacheDir as String, 'package.json')
            doLast {
                println "[ontrack] Copies the package.json file to ${project.ontrackCacheDir}"
                project.mkdir new File(project.ontrackCacheDir as String)
                new File(project.ontrackCacheDir as String, 'package.json').text = getClass().getResourceAsStream('/extension/package.json').text
            }
        }

        project.tasks.create('copyGulpFile') {
            // Without any input reference, the target file will never be replaced
            // outputs.file new File(project.ontrackCacheDir as String, 'gulpfile.js')
            doLast {
                println "[ontrack] Copies the gulpfile.js file to ${project.ontrackCacheDir}"
                project.mkdir new File(project.ontrackCacheDir as String)
                new File(project.ontrackCacheDir as String, 'gulpfile.js').text = getClass().getResourceAsStream('/extension/gulpfile.js').text
            }
        }

        project.tasks.create('npmPackages', NpmTask) {
            def nodeModulesDir = new File(project.ontrackCacheDir as String, 'node_modules')

            dependsOn project.tasks.npmCacheConfig
            dependsOn project.tasks.copyPackageJson

            inputs.file new File(project.ontrackCacheDir as String, 'package.json')
            outputs.dir nodeModulesDir

            doFirst { println "[ontrack] Install Node.js packages in ${nodeModulesDir}" }

            workingDir = new File(project.ontrackCacheDir as String)
            args = [ 'install' ]
        }

        /**
         * Gulp call
         */

        project.tasks.create('web', NodeTask) {
            dependsOn 'npmPackages'
            dependsOn 'copyGulpFile'

            inputs.dir project.file('src/main/resources/static')
            outputs.file project.file('build/web/dist/module.js')

            doFirst {
                project.mkdir project.buildDir
                println "[ontrack] Generating web resources of ${project.extensions.ontrack.id(project)} in ${project.buildDir}"
            }

            workingDir = new File(project.ontrackCacheDir as String)
            script = new File(new File(project.ontrackCacheDir as String, 'node_modules'), 'gulp/bin/gulp')
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

        /**
         * Custom configuration to package a module's own dependencies, minus the ones
         * provided by Ontrack itself.
         */

//        project.configurations {
//            moduleDependencies {
//                extendsFrom project.configurations.runtime
//                exclude group: 'net.nemerosa.ontrack'
//                exclude group: 'org.springframework'
//                exclude group: 'org.apache.httpcomponents'
//                exclude group: 'org.codehaus.groovy'
//                exclude group: 'org.projectlombok'
//                exclude group: 'org.slf4j'
//                exclude group: 'commons-io'
//                exclude group: 'com.google.guava'
//            }
//        }

        /**
         * Spring Boot packaging as a module
         */

//        project.apply plugin: 'spring-boot'
//        project.springBoot {
//            layout = 'MODULE'
//            customConfiguration = 'moduleDependencies'
//        }

    }

}
