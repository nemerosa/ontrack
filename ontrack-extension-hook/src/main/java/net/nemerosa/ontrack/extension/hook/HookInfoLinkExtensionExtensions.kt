package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.json.asJson

fun HookInfoLinkExtension.createHookInfoLink(data: Any) = HookInfoLink(
        feature = feature.id,
        id = id,
        data = data.asJson(),
)