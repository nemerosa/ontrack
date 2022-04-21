package net.nemerosa.ontrack.extension.notifications.mail

import com.icegreen.greenmail.util.GreenMailUtil
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MailServiceIT : AbstractMailTestSupport() {

    @Test
    fun `Sending a mail`() {
        val subject = uid("mail")
        val sent = mailService.sendMail(
            to = DEFAULT_ADDRESS,
            subject = subject,
            body = "Mail $subject",
        )
        // Basic check
        assertTrue(sent, "Mail indicated as being sent")
        // Gets the received mails
        val messages = greenMail.receivedMessages
        // ... and check we retrieve the sent mail
        assertNotNull(messages.find {
            it.subject == subject
        }, "Mail received") { message ->
            assertEquals("Mail $subject", GreenMailUtil.getBody(message))
        }
    }

}