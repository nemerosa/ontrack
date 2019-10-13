import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    implementation(project(":ontrack-extension-scm"))
    implementation(project(":ontrack-repository-support"))
    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-tx"))
    implementation("org.springframework:spring-tx")
    implementation("org.tmatesoft.svnkit:svnkit:1.8.12")
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