package net.nemerosa.ontrack.model.templating

fun <T : TemplatingContext> TemplatingContextHandler<T>.createTemplatingContextData(data: T) =
    TemplatingContextData(
        id = id,
        data = serialize(data),
    )
