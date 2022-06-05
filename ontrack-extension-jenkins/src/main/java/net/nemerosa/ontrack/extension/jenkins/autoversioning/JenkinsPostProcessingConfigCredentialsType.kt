package net.nemerosa.ontrack.extension.jenkins.autoversioning

/**
 * Types of supported credentials
 *
 * @property jenkinsName Name of the credentials type in Jenkins
 * @property check Check on the bound variables
 */
enum class JenkinsPostProcessingConfigCredentialsType(
    val jenkinsName: String,
    val check: (List<String>) -> Unit,
) {

    USERNAME_PASSWORD("usernamePassword", { vars ->
        if (vars.size != 2 || vars.any { it.isBlank() }) throw IllegalArgumentException("Requires 2 bound variables")
    }),
    USERNAME_COLON_PASSWORD("usernameColonPassword", { vars ->
        if (vars.size != 1 || vars.any { it.isBlank() }) throw IllegalArgumentException("Requires 1 bound variable")
    }),
    STRING("string", { vars ->
        if (vars.size != 1 || vars.any { it.isBlank() }) throw IllegalArgumentException("Requires 1 bound variable")
    })

}