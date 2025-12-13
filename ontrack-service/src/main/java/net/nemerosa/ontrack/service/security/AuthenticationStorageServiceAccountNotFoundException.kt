package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.BaseException

class AuthenticationStorageServiceAccountNotFoundException(accountEmail: String) :
    BaseException("Account with email $accountEmail cannot be found.")
