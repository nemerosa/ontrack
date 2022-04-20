package net.nemerosa.ontrack.extension.notifications.mail

import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import io.mockk.mockk
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

abstract class AbstractMailTestSupport() {

    /**
     * Configures a [MailService] for a local mail server.
     */
    protected fun withMail(
        code: (mailService: MailService, greenMail: GreenMailExtension) -> Unit,
    ) {
        val mailService = DefaultMailService(
            javaMailSender = createJavaMailSender(),
            applicationLogService = mockk(),
        )
        code(mailService, greenMail)
    }

    private fun createJavaMailSender(): JavaMailSender {
        return JavaMailSenderImpl().apply {
            host = "localhost"
            port = greenMail.smtp.port
        }
    }

    companion object {
        const val DEFAULT_ADDRESS = "to@localhost"

        @RegisterExtension
        val greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP)
    }

}

