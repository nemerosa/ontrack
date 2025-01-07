package net.nemerosa.ontrack.extension.environments

data class SlotDeploymentActionStatus(
    val ok: Boolean,
    val message: String,
) {
    companion object {

        fun ok(message: String) = SlotDeploymentActionStatus(
            ok = true,
            message = message,
        )

        fun nok(message: String) = SlotDeploymentActionStatus(
            ok = false,
            message = message,
        )
    }
}
