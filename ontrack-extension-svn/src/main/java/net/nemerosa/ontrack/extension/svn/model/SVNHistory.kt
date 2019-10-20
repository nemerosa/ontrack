package net.nemerosa.ontrack.extension.svn.model

import com.fasterxml.jackson.annotation.JsonIgnore

class SVNHistory(
        val references: List<SVNReference>
) {
    val revision: Long
        @JsonIgnore
        get() = references[0].revision

    constructor() : this(emptyList<SVNReference>())

    constructor(vararg references: SVNReference) : this(listOf<SVNReference>(*references))

    fun add(reference: SVNReference): SVNHistory = SVNHistory(
            references + reference
    )

    fun truncateAbove(index: Int): SVNHistory {
        return SVNHistory(references.subList(index + 1, references.size))
    }
}
