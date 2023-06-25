package net.nemerosa.ontrack.model.security

/**
 * Service used to collect the authorizations for the current used,
 * in order to be used at client side.
 *
 * The authorizations are given for information only, the security is
 * always checked on server side.
 */
interface AuthorizationService {

    val authorizations: List<Authorization>

}