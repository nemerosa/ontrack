package net.nemerosa.ontrack.extension.notifications.mail

import com.icegreen.greenmail.spring.GreenMailBean
import com.icegreen.greenmail.util.GreenMail
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

abstract class AbstractMailTestSupport : AbstractNotificationTestSupport() {

    @Autowired
    protected lateinit var mailService: MailService

    @Autowired
    protected lateinit var greenMail: GreenMail


    companion object {
        const val DEFAULT_ADDRESS = "to@localhost"
    }

    @Configuration
    @Profile(RunProfile.UNIT_TEST)
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

    }

}

