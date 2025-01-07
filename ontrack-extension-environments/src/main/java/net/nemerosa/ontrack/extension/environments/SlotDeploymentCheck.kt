package net.nemerosa.ontrack.extension.environments

/**
 * Result of a check for a deployment.
 */
data class SlotDeploymentCheck(
    val ok: Boolean,
    val overridden: Boolean,
    val reason: String?,
) {
    companion object {

        fun ok(reason: String? = null) = SlotDeploymentCheck(
            ok = true,
            overridden = false,
            reason = reason,
        )

        fun nok(reason: String?) = SlotDeploymentCheck(
            ok = false,
            overridden = false,
            reason = reason,
        )

        fun check(check: Boolean, ok: String, nok: String) = SlotDeploymentCheck(
            ok = check,
            overridden = false,
            reason = if (check) ok else nok,
        )

    }
}