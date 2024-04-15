package net.nemerosa.ontrack.graphql.schema.jobs

data class JobActionResult(
    val ok: Boolean,
    val error: String?,
) {
    companion object {
        fun ok() =
            JobActionResult(
                ok = true,
                error = null,
            )

        fun check(ok: Boolean, error: String) =
            JobActionResult(
                ok = ok,
                error = error.takeIf { !ok }
            )
    }
}