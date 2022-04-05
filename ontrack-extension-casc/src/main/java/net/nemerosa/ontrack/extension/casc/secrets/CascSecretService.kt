package net.nemerosa.ontrack.extension.casc.secrets

interface CascSecretService {

    fun getValue(ref: String): String

}