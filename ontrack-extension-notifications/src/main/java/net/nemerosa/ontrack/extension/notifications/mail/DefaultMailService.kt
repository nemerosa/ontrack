package net.nemerosa.ontrack.extension.notifications.mail

import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Component
import javax.mail.Message


@Component
@ConditionalOnBean(JavaMailSender::class)
class DefaultMailService(
    private val javaMailSender: JavaMailSender,
    private val applicationLogService: ApplicationLogService,
) : MailService {

    override fun sendMail(to: String, cc: String?, subject: String, body: String?): Boolean = try {
        val preparator = MimeMessagePreparator { mimeMessage ->
            mimeMessage.setRecipients(Message.RecipientType.TO, to)
            if (!cc.isNullOrBlank()) {
                mimeMessage.setRecipients(Message.RecipientType.CC, cc)
            }
            // TODO mimeMessage.setFrom(InternetAddress("mail@mycompany.example"))
            mimeMessage.subject = subject
            mimeMessage.setText(body, Charsets.UTF_8.name(), "html")
        }
        javaMailSender.send(preparator)
        // OK
        true
    } catch (ex: Exception) {
        // Logs the error
        applicationLogService.log(
            ApplicationLogEntry.error(
                ex,
                NameDescription.nd("mail-error", "Mail notification error"),
                "Cannot send mail: ${ex.message}"
            )
        )
        // Not sent
        false
    }

}