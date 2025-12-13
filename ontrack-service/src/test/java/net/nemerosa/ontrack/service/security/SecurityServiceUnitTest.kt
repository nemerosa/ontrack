package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.ProjectCreation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SecurityServiceUnitTest {

    private lateinit var securityService: SecurityServiceImpl

    @BeforeEach
    fun before() {
        securityService = SecurityServiceImpl()
    }

    private fun protectedCall(): Boolean {
        securityService.checkGlobalFunction(ProjectCreation::class.java)
        return true
    }

    @Test
    fun run_as_admin_not_applied() {
        assertFailsWith<AccessDeniedException> {
            protectedCall()
        }
    }

    @Test
    fun run_as_admin() {
        assertTrue(securityService.runAsAdmin { protectedCall() }())
    }
}