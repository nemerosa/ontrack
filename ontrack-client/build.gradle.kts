plugins {
    `java-library`
}

description = "Generic customisable HTTP and JSON client."

dependencies {
    api(project(":ontrack-common"))
    api(project(":ontrack-json"))
    api("org.apache.httpcomponents:httpclient")

    implementation("org.apache.httpcomponents:httpmime")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.slf4j:slf4j-api")
}
