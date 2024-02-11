package net.nemerosa.ontrack.extension.notifications.mail

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.textField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class MailNotificationChannel(
    javaMailSender: JavaMailSender?,
    private val mailService: MailService,
    private val htmlNotificationEventRenderer: HtmlNotificationEventRenderer,
    private val eventTemplatingService: EventTemplatingService,
) : AbstractNotificationChannel<MailNotificationChannelConfig>(MailNotificationChannelConfig::class) {

    private val logger: Logger = LoggerFactory.getLogger(MailNotificationChannel::class.java)

    override val type: String = "mail"
    override val enabled: Boolean = javaMailSender != null

    @PostConstruct
    fun log() {
        logger.info("Mail notification channel enabled.")
    }

    override fun toSearchCriteria(text: String): JsonNode =
        mapOf(
            MailNotificationChannelConfig::subject.name to text
        ).asJson()

    override fun getForm(c: MailNotificationChannelConfig?): Form = Form.create()
        .textField(MailNotificationChannelConfig::to, c?.to)
        .textField(MailNotificationChannelConfig::cc, c?.cc)
        .textField(MailNotificationChannelConfig::subject, c?.subject)

    override fun toText(config: MailNotificationChannelConfig): String = config.subject

    override fun publish(config: MailNotificationChannelConfig, event: Event, template: String?): NotificationResult {
        // Subject as a template
        val subject = eventTemplatingService.render(
            template = config.subject,
            event = event,
            renderer = PlainEventRenderer.INSTANCE, // Using plain text for the subject
        )
        // Formatting the message
        val message = eventTemplatingService.renderEvent(
            event = event,
            template = template,
            renderer = htmlNotificationEventRenderer,
        )
        // Sending the message
        val sent = mailService.sendMail(
            to = config.to,
            cc = config.cc,
            subject = subject,
            body = message,
        )
        // Result
        return if (sent) {
            NotificationResult.ok()
        } else {
            NotificationResult.error("Mail could not be sent. Check the operational logs.")
        }
    }
}