package net.nemerosa.ontrack.extension.notifications.mail

interface MailService {

    /**
     * Sends a mail.
     */
    fun sendMail(to: String, cc: String?, subject: String, body: String?): Boolean

}