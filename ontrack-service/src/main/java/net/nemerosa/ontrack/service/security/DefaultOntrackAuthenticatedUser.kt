package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.Authorisations
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser
import net.nemerosa.ontrack.model.security.OntrackUser
import org.springframework.security.core.userdetails.UserDetails

class DefaultOntrackAuthenticatedUser(
        override val user: OntrackUser,
        val authorisations: Authorisations
) : OntrackAuthenticatedUser, UserDetails by user
