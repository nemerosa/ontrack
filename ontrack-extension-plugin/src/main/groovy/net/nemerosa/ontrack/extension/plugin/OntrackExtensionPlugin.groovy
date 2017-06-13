package net.nemerosa.ontrack.extension.plugin

import com.moowork.gradle.node.task.NodeTask
import com.moowork.gradle.node.task.NpmTask
import org.gradle.api.Plugin
import org.gradle.api.Project
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
        String kotlinVersion = properties.getProperty('kotlin-version')
        project.ext.ontrackVersion = ontrackVersion
        project.ext.kotlinVersion = kotlinVersion

        println "[ontrack] Applying Ontrack plugin v${ontrackVersion} to ${project.name}"

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
         * Kotlin dependencies
         */

        if (project.extensions.ontrack.kotlin) {
            project.apply plugin: 'kotlin'
            project.dependencies {
                compile "org.jetbrains.kotlin:kotlin-stdlib-jre8:${kotlinVersion}"
            }
        }

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
                println "[ontrack] Generating web resources of ${project.extensions.ontrack.id()} in ${project.buildDir}"
            }

            workingDir = new File(project.ontrackCacheDir as String)
            script = new File(new File(project.ontrackCacheDir as String, 'node_modules'), 'gulp/bin/gulp')
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
         * Spring Boot packaging as a module
         */

        project.apply plugin: 'spring-boot'
        project.springBoot {
            layout = 'MODULE'
            customConfiguration = 'moduleDependencies'
        }

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
