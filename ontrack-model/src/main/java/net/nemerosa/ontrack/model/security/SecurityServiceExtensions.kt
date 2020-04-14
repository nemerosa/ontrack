package net.nemerosa.ontrack.model.security

/**
 * More Kotlin-friendly call to [SecurityService]
 */
@Deprecated("Native methods can be used instead.", replaceWith = ReplaceWith("asAdmin"))
fun <T> SecurityService.callAsAdmin(call: () -> T): T = asAdmin(call)
