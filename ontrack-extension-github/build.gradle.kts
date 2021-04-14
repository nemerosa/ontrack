import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-git"))
    api("org.eclipse.mylyn.github:org.eclipse.egit.github.core:2.1.5")

    implementation(project(":ontrack-ui-graphql"))
    implementation("org.apache.commons:commons-lang3")
    implementation(project(":ontrack-extension-casc"))

    testImplementation(project(":ontrack-test-utils"))
    testImplementation(project(":ontrack-it-utils"))
    testImplementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.codehaus.groovy:groovy")
    testImplementation(project(path = ":ontrack-extension-issues", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}