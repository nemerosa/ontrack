package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.common.asMap
import net.nemerosa.ontrack.model.structure.SearchItem

data class ScmIssueSearchItem(
    val projectName: String,
    val key: String,
    val displayKey: String
) : SearchItem {
    override val id: String = "$projectName::$key"

    override val fields: Map<String, Any?> = asMap(
        this::projectName,
        this::key,
        this::displayKey
    )
}