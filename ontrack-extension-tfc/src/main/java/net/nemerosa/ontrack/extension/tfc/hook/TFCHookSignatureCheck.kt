package net.nemerosa.ontrack.extension.tfc.hook

/**
 * Possible results for a signature check
 */
enum class TFCHookSignatureCheck {

    /**
     * Signature was checked OK.
     */
    OK,
    /**
     * Signature is not OK.
     */
    MISMATCH,
    /**
     * Signature cannot be checked because the settings do not contain the signature token.
     */
    MISSING_TOKEN,

}