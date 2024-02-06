package net.nemerosa.ontrack.extension.notifications.mail

import com.icegreen.greenmail.spring.GreenMailBean
import com.icegreen.greenmail.util.GreenMail
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

class AbstractMailTestSupportConfiguration {

    @Bean
    fun greenMailBean(): GreenMailBean = GreenMailBean()

    @Bean
    fun greenMail(greenMailBean: GreenMailBean): GreenMail = greenMailBean.greenMail

    @Bean
    fun mailSender(greenMail: GreenMail): JavaMailSender = JavaMailSenderImpl().apply {
        host = "localhost"
        port = greenMail.smtp.port
    }

}