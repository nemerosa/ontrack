package net.nemerosa.ontrack.extension.notifications.mail

import com.icegreen.greenmail.spring.GreenMailBean
import com.icegreen.greenmail.util.GreenMail
import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.rendering.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

class AbstractMailTestSupportConfiguration {

    @Bean
    fun greenMailBean(): GreenMailBean = GreenMailBean()

    @Bean
    fun greenMail(greenMailBean: GreenMailBean): GreenMail = greenMailBean.greenMail

    @Bean
    fun javaMailSender(greenMail: GreenMail): JavaMailSender = JavaMailSenderImpl().apply {
        host = "localhost"
        port = greenMail.smtp.port
    }

    @Bean
    fun mailService(
        javaMailSender: JavaMailSender,
        applicationLogService: ApplicationLogService,
    ): MailService = DefaultMailService(
        javaMailSender,
        applicationLogService,
        NotificationsConfigProperties(),
    )

    @Bean
    fun mailNotificationChannel(
        mailService: MailService,
        htmlNotificationEventRenderer: HtmlNotificationEventRenderer,
    ) = MailNotificationChannel(
        mailService,
        htmlNotificationEventRenderer,
    )

}