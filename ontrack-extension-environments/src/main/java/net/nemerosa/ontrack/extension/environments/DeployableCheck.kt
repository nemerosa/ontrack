package net.nemerosa.ontrack.extension.environments

data class DeployableCheck(
    val status: Boolean,
    val reason: String?,
) {
    companion object {

        fun ok() = DeployableCheck(
            status = true,
            reason = null,
        )

        fun nok(reason: String?) = DeployableCheck(
            status = false,
            reason = reason,
        )

        fun check(check: Boolean, ok: String, nok: String) = DeployableCheck(
            status = check,
            reason = if (check) ok else nok,
        )

    }
}