package net.nemerosa.ontrack.gradle

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GitterAnnouncement : DefaultTask() {

    /**
     * Gitter URL
     */
    @Input
    var url = "https://api.gitter.im"

    /**
     * Gitter token used for authentication
     */
    @Input
    var token: String = ""

    /**
     * ID of the room where to send the message
     */
    @Input
    var roomId: String = ""

    /**
     * Registers the text to send
     */
    @Input
    var text: () -> String = { error("Text is required") }

    /**
     * Sends the message on Gitter
     */
    @TaskAction
    fun run() {
        // Checks
        if (token.isBlank()) error("Gitter token is required")
        if (roomId.isBlank()) error("Gitter room ID is required")
        // Getting the change log as a message
        val message = text()
        // Payload as JSON
        val payload = mapOf("text" to message)
        val jsonPayload = ObjectMapper().writeValueAsString(payload)
        // Posting the message in the room
        val httpClient = HttpClients.createDefault()
        val httpPost = HttpPost("$url/v1/rooms/$roomId/chatMessages")
        httpPost.addHeader("Authorization", "Bearer $token")
        httpPost.entity = StringEntity(
            jsonPayload,
            ContentType.create("application/json", "UTF-8")
        )
        val httpResponse = httpClient.execute(httpPost)
        if (httpResponse.statusLine?.statusCode != HttpStatus.SC_OK) {
            error("Could not send message on Gitter: ${httpResponse.statusLine}")
        }
    }

}