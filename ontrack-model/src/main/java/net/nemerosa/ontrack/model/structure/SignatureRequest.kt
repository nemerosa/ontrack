package net.nemerosa.ontrack.model.structure

import java.time.LocalDateTime

data class SignatureRequest(val time: LocalDateTime?, val user: String?) {

    fun getSignature(signature: Signature): Signature =
        Signature(
            time = time ?: signature.time,
            user = user?.takeIf { it.isNotBlank() }?.let { User.of(it) } ?: signature.user
        )

    companion object {
        fun of(signature: Signature): SignatureRequest {
            return SignatureRequest(signature.time, signature.user.name)
        }
    }
}
