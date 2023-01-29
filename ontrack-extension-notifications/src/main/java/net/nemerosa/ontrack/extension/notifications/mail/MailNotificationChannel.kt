package net.nemerosa.ontrack.extension.notifications.mail

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.SimpleExpand
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.notifications.rendering.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.textField
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
@ConditionalOnBean(JavaMailSender::class)
class MailNotificationChannel(
    private val mailService: MailService,
    private val htmlNotificationEventRenderer: HtmlNotificationEventRenderer,
) : AbstractNotificationChannel<MailNotificationChannelConfig>(MailNotificationChannelConfig::class) {

    override val type: String = "mail"
    override val enabled: Boolean = true

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
        val parameters = event.getTemplateParameters(caseVariants = true)
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