package net.nemerosa.ontrack.model.security

import kotlin.reflect.KClass

interface AccountACLService {

    fun getAuthorizations(account: Account): Authorisations

    fun getGroups(account: Account): List<AuthorizedGroup>

    /**
     * List of [project functions][ProjectFunction] which are automatically assigned to authenticated users.
     */
    val autoProjectFunctions: Set<KClass<out ProjectFunction>>

    /**
     * List of [global functions][GlobalFunction] which are automatically assigned to authenticated users.
     */
    val autoGlobalFunctions: Set<KClass<out GlobalFunction>>

}