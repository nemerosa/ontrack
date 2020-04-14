package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.ProjectCreation
import org.junit.Before
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertTrue

class SecurityServiceUnitTest {

    private lateinit var securityService: SecurityServiceImpl

    @Before
    fun before() {
        securityService = SecurityServiceImpl()
    }

    private fun protectedCall(): Boolean {
        securityService.checkGlobalFunction(ProjectCreation::class.java)
        return true
    }

    @Test(expected = AccessDeniedException::class)
    fun run_as_admin_not_applied() {
        protectedCall()
    }

    @Test
    fun run_as_admin() {
        assertTrue(securityService.runAsAdmin<Boolean> { protectedCall() }())
    }
}