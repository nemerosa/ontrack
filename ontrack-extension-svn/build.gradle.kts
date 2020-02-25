import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-scm"))
    api(project(":ontrack-repository-support"))
    api("org.tmatesoft.svnkit:svnkit:1.8.12")

    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-tx"))
    implementation("org.springframework:spring-tx")
    implementation("org.apache.commons:commons-lang3")
    implementation("commons-io:commons-io")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(path = ":ontrack-extension-issues", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testImplementation("org.codehaus.groovy:groovy")

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}