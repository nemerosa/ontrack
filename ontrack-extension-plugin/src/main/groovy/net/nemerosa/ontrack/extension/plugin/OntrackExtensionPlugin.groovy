package net.nemerosa.ontrack.extension.plugin

import com.liferay.gradle.plugins.node.tasks.ExecuteNodeScriptTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec

/**
 * Plugin to create, manage, package and test an Ontrack extension.
 */
class OntrackExtensionPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        /**
         * Reading the version information
         */

        Properties properties = new Properties()
        getClass().getResourceAsStream('/META-INF/gradle-plugins/ontrack.properties').withStream { properties.load(it) }
        String ontrackVersion = properties.getProperty('implementation-version')
        String theKotlinVersion = properties.getProperty('kotlin-version')
        project.ext.ontrackVersion = ontrackVersion

        println "[ontrack] Applying Ontrack plugin v${ontrackVersion} to ${project.name}"

        /**
         * Kotlin version
         */

        project.ext.kotlinVersion = theKotlinVersion

        /**
         * Java project
         */

        project.apply plugin: 'java'

        /**
         * Default dependencies
         */

        project.configurations {
            ontrack {
                extendsFrom runtime
            }
        }

        project.dependencies {
            compile "net.nemerosa.ontrack:ontrack-extension-support:${ontrackVersion}"

            testCompile "net.nemerosa.ontrack:ontrack-it-utils:${ontrackVersion}"
            testRuntime "net.nemerosa.ontrack:ontrack-service:${ontrackVersion}"
            testRuntime "net.nemerosa.ontrack:ontrack-repository-impl:${ontrackVersion}"

            ontrack "net.nemerosa.ontrack:ontrack-ui:${ontrackVersion}"
        }

        /**
         * Project's configuration
         */

        project.extensions.create('ontrack', OntrackExtension, project)

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
                println "[ontrack] Generating web resources of ${project.extensions.ontrack.id()} in ${project.buildDir}"
            }

            workingDir = project.projectDir
            scriptFile = project.file('node_modules/gulp/bin/gulp')
            args = [
                    'default',
                    '--extension', project.extensions.ontrack.id(),
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
ontrack = ${ontrackVersion}
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
                into { "static/extension/${project.extensions.ontrack.id()}/${project.version}/" }
            }
            from('build') {
                include 'ontrack.properties'
                into "META-INF/ontrack/extension/"
                rename { "${project.extensions.ontrack.id()}.properties" }
            }
            exclude 'static/**/*.js'
            exclude 'static/**/*.html'
        }

        /**
         * Custom configuration to package a module's own dependencies, minus the ones
         * provided by Ontrack itself.
         */

        project.configurations {
            moduleDependencies {
                extendsFrom project.configurations.runtime
                exclude group: 'net.nemerosa.ontrack'
                exclude group: 'org.springframework'
                exclude group: 'org.apache.httpcomponents'
                exclude group: 'org.codehaus.groovy'
                exclude group: 'org.projectlombok'
                exclude group: 'org.slf4j'
                exclude group: 'commons-io'
                exclude group: 'com.google.guava'
            }
        }

        /**
         * Task to copy the dependencies
         */

        project.tasks.create('ontrackCopyDependencies', Copy) {
            from project.configurations.moduleDependencies
            into 'build/dist'
        }

        /**
         * Task to prepare dependencies & main JAR in dist folder
         */
        project.tasks.create('ontrackDist', Copy) {
            dependsOn 'ontrackCopyDependencies'
            from project.tasks.jar
            into 'build/dist'
        }

        /**
         * Building launches the `ontrackDist` task
         */
        project.tasks.build.dependsOn 'ontrackDist'

        /**
         * Running the extension in Ontrack
         */

        project.tasks.create('ontrackRun', JavaExec) {
            classpath = project.configurations.ontrack + project.sourceSets.main.runtimeClasspath
            // Using explicitely the Spring Boot launcher
            main = 'org.springframework.boot.loader.PropertiesLauncher'
            systemProperties = [
                    'loader.main': 'net.nemerosa.ontrack.boot.Application'
            ]
            // Local database
            args '--spring.datasource.url=jdbc:h2:./work/ontrack/db/data;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;DEFRAG_ALWAYS=TRUE'
        }

    }
}
