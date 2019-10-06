import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

apply<OntrackExtensionPlugin>()

dependencies {
    compile(project(":ontrack-extension-support"))
    compile("org.springframework:spring-tx")
}
