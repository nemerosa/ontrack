package net.nemerosa.ontrack.extension.github.client

import org.apache.commons.codec.binary.Base64

data class GitHubFile(
    /**
     * Base64 encoded content
     */
    val content: String,
    /**
     * SHA of the file
     */
    val sha: String,
) {

    fun contentAsBinary(): ByteArray = Base64.decodeBase64(content)

    fun contentAsLines(): List<String> = String(contentAsBinary()).lines()

}


