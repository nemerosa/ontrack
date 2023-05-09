package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.json.asJson

fun <T> HookInfoLinkExtension<T>.createHookInfoLink(data: T) = HookInfoLink(
        feature = feature.id,
        id = id,
        data = data.asJson(),
)