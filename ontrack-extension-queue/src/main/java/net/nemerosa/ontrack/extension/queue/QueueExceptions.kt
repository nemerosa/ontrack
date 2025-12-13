package net.nemerosa.ontrack.extension.queue

import net.nemerosa.ontrack.common.BaseException

abstract class QueuePayloadException(qp: QueuePayload, reason: String) : BaseException(
    "Issue with queue payload (id=${qp.id}, processor=${qp.processor}): ${reason})"
)

class QueuePayloadAccountNameNotFoundException(
    qp: QueuePayload
) : QueuePayloadException(qp, "Account name not found for the queue payload")
