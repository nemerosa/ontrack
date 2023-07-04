package net.nemerosa.ontrack.extension.slack.service

enum class SlackNotificationType(
    val color: String?,
) {

    INFO(null),

    SUCCESS("#0d0"),

    WARNING("#fc0"),

    ERROR("#f55");


}