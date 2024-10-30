package net.nemerosa.ontrack.extension.environments

data class SlotAdmissionRuleInputFieldTypeValidation(
    val ok: Boolean,
    val message: String?,
) {
    companion object {
        fun ok() = SlotAdmissionRuleInputFieldTypeValidation(ok = true, message = null)
        fun nok(message: String? = null) = SlotAdmissionRuleInputFieldTypeValidation(ok = false, message = message)
        fun check(check: Boolean, message: String? = null) =
            if (check) {
                ok()
            } else {
                nok(message)
            }
    }
}
