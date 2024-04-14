package net.nemerosa.ontrack.extension.notifications.mail.mock

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.notifications.mail.MailService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.ACC)
class MockMailService : MailService {

    private val mails = mutableListOf<MockMail>()

    override fun sendMail(to: String, cc: String?, subject: String, body: String?): Boolean {
        mails += MockMail(
            to = to,
            cc = cc,
            subject = subject,
            body = body
        )
        return true
    }

    fun find(to: String?, subject: String?): MockMail? {
        return mails.find { mail ->
            (to.isNullOrBlank() || to == mail.to) &&
                    (subject.isNullOrBlank() || mail.subject.matches(subject.toRegex()))
        }
    }

}