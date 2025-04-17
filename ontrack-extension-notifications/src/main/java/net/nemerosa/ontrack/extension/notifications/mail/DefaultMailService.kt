package net.nemerosa.ontrack.extension.notifications.mail

import jakarta.mail.Message
import jakarta.mail.internet.InternetAddress
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Component


@Component
@Profile(RunProfile.PROD)
class DefaultMailService(
    private val javaMailSender: JavaMailSender?,
    private val applicationLogService: ApplicationLogService,
    private val notificationsConfigProperties: NotificationsConfigProperties,
) : MailService {

    private val logger: Logger = LoggerFactory.getLogger(DefaultMailService::class.java)

    override fun sendMail(to: String, cc: String?, subject: String, body: String?): Boolean = try {
        if (javaMailSender != null) {
            val preparator = MimeMessagePreparator { mimeMessage ->
                mimeMessage.setRecipients(Message.RecipientType.TO, to)
                if (!cc.isNullOrBlank()) {
                    mimeMessage.setRecipients(Message.RecipientType.CC, cc)
                }
                mimeMessage.setFrom(InternetAddress(notificationsConfigProperties.mail.from))
                mimeMessage.subject = subject
                mimeMessage.setText(body, Charsets.UTF_8.name(), "html")
            }
            javaMailSender.send(preparator)
            // OK
            true
        } else {
            logger.warn("Mails cannot be sent because mail sender is not available.")
            false
        }
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