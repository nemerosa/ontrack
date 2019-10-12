dependencies {
    compile(project(":ontrack-model"))
    compile(project(":ontrack-extension-api"))
    compile("org.springframework:spring-webmvc")
    compile("org.springframework.boot:spring-boot-starter-web")

    testCompile(project(path = ":ontrack-model", configuration = "tests"))
}
