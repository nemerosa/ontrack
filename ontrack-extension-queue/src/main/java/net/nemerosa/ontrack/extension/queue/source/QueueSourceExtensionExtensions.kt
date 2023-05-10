package net.nemerosa.ontrack.extension.queue.source

import net.nemerosa.ontrack.json.asJson

fun <T> QueueSourceExtension<T>.createQueueSource(data: T) =
        QueueSource(
                feature = feature.id,
                id = id,
                data = data.asJson(),
        )
