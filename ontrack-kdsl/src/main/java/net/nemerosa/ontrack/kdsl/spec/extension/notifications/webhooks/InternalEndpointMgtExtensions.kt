package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

fun InternalEndpointMgt.testOk(
    webhookName: String,
    content: String,
    delayMs: Long? = null,
) = test(
    TestPayloadWrapper(
        webhook = webhookName,
        payload = TestPayload(
            mode = TestPayloadMode.OK,
            content = content,
            delayMs = delayMs,
        )
    )
)

fun InternalEndpointMgt.testNotFound(
    webhookName: String,
    content: String,
) = test(
    TestPayloadWrapper(
        webhook = webhookName,
        payload = TestPayload(
            mode = TestPayloadMode.NOT_FOUND,
            content = content,
        )
    )
)

fun InternalEndpointMgt.testError(
    webhookName: String,
    content: String,
) = test(
    TestPayloadWrapper(
        webhook = webhookName,
        payload = TestPayload(
            mode = TestPayloadMode.INTERNAL_ERROR,
            content = content,
        )
    )
)
