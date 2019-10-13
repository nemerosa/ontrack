package net.nemerosa.ontrack.extension.api.model

import net.nemerosa.ontrack.model.structure.ID

open class BuildDiffRequest {

    /**
     * Build ID
     */
    var from: ID? = null

    /**
     * Build ID
     */
    var to: ID? = null

    fun withFrom(value: ID?) = apply { from = value }
    fun withTo(value: ID?) = apply { to = value }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildDiffRequest

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from?.hashCode() ?: 0
        result = 31 * result + (to?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "BuildDiffRequest(from=$from, to=$to)"
    }


}
