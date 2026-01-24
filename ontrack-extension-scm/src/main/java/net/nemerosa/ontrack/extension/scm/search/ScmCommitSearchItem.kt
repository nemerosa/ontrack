package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.common.asMap
import net.nemerosa.ontrack.model.structure.SearchItem

data class ScmCommitSearchItem(
    val projectName: String,
    override val id: String,
    val shortId: String,
    val author: String,
    val message: String,
) : SearchItem {

    override val fields: Map<String, Any?> = asMap(
        this::projectName,
        this::id,
        this::shortId,
        this::author,
        this::message,
    )

}
