package net.nemerosa.ontrack.extension.notifications.mail

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.SimpleExpand
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventVariableService
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
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
    private val eventVariableService: EventVariableService,
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

    override fun publish(config: MailNotificationChannelConfig, event: Event): NotificationResult {
        // Subject as a template
        val parameters = eventVariableService.getTemplateParameters(event, caseVariants = true)
        val subject = SimpleExpand.expand(config.subject, parameters)
        // Formatting the message
        val message = event.render(htmlNotificationEventRenderer)
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