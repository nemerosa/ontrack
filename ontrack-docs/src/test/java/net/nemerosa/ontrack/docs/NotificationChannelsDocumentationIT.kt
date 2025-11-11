package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.extension.notifications.channels.NoTemplate
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.getDocumentationExampleCode
import net.nemerosa.ontrack.model.docs.getFieldsDocumentation
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.hasAnnotation

class NotificationChannelsDocumentationIT : AbstractDocGenIT() {

    @Autowired
    private lateinit var notificationChannels: List<NotificationChannel<*, *>>

    @Test
    fun `Notifications list`() {

        fun getNotificationChannelFileId(channel: NotificationChannel<*, *>): String {
            return "notification-backend-${channel.type}"
        }

        fun getNotificationChannelTitle(channel: NotificationChannel<*, *>): String {
            return "${channel.displayName} (`${channel.type}`)"
        }

        fun <C, R> generateNotificationChannel(
            directoryContext: DocGenDirectoryContext,
            channel: NotificationChannel<C, R>
        ) {
            val description = getAPITypeDescription(channel::class)
            val parameters = getFieldsDocumentation(channel::class)
            val example = getDocumentationExampleCode(channel::class)

            val fileId = getNotificationChannelFileId(channel)

            directoryContext.writeFile(
                fileId = fileId,
                level = 4,
                title = getNotificationChannelTitle(channel),
                header = description,
                fields = parameters,
                example = example,
                links = channel::class.findAnnotations(),
                extendedConfig = { s ->
                    val output = getFieldsDocumentation(channel::class, section = "output")
                    if (output.isNotEmpty()) {
                        s.append("Output:\n\n")
                        directoryContext.writeFields(s, output)
                    }
                },
                extendedHeader = { s ->
                    if (channel::class.hasAnnotation<NoTemplate>()) {
                        s.append("\n\n_This channel does not use the custom template._\n\n")
                    }
                }
            )
        }

        docGenSupport.inDirectory("notifications") {

            writeIndex(
                fileId = "appendix-notifications-backends",
                level = 4,
                title = "List of notification backends",
                items = notificationChannels.associate { channel ->
                    getNotificationChannelFileId(channel) to getNotificationChannelTitle(channel)
                }
            )

            notificationChannels.forEach { channel ->
                generateNotificationChannel(this, channel)
            }

        }
    }

}