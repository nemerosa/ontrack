package net.nemerosa.ontrack.graphql.schema.actions

import java.net.URI

/**
 * An `ActionLink` refers to a HTTP end point,
 * having an [uri], a HTTP [method] (like `PUT`
 * or `POST`) and a [type].
 * The  [type] identifies the type of action,
 * like "download", "create", "update", "delete", etc.
 */
class UIActionLink<T>(
        val type: String,
        val description: String,
        val method: String,
        val uri: (T) -> URI?
)
