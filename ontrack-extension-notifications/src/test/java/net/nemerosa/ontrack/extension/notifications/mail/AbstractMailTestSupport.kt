package net.nemerosa.ontrack.extension.notifications.mail

import com.icegreen.greenmail.util.GreenMail
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [AbstractMailTestSupportConfiguration::class])
@DirtiesContext
abstract class AbstractMailTestSupport : AbstractNotificationTestSupport() {

    @Autowired
    protected lateinit var mailService: MailService

    @Autowired
    protected lateinit var greenMail: GreenMail


    companion object {
        const val DEFAULT_ADDRESS = "to@localhost"
    }

}

