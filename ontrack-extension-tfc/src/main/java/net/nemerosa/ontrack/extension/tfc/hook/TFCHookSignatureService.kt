package net.nemerosa.ontrack.extension.tfc.hook

interface TFCHookSignatureService {

    /**
     * Checks the TFE hook signature.
     *
     * See https://developer.hashicorp.com/terraform/cloud-docs/api-docs/notification-configurations#notification-authenticity
     *
     * @param body Raw body of the payload
     * @param signature Signature for the payload
     * @return Result of the signature check
     */
    fun checkPayloadSignature(body: String, signature: String): TFCHookSignatureCheck

}