plugins {
    `java-library`
}

description = "Generic customisable HTTP and JSON client."

dependencies {
    api(project(":ontrack-common"))
    api(project(":ontrack-json"))
    api("org.apache.httpcomponents.client5:httpclient5")
    api("org.apache.httpcomponents.core5:httpcore5")

    implementation("org.apache.commons:commons-lang3")
    implementation("org.slf4j:slf4j-api")
}
