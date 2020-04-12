package net.nemerosa.ontrack.model.security

import java.util.function.Supplier

/**
 * More Kotlin-friendly call to [SecurityService]
 */
fun <T> SecurityService.callAsAdmin(call: () -> T): T = asAdmin(Supplier { call() })
