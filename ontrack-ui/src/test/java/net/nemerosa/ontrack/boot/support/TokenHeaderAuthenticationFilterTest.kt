package net.nemerosa.ontrack.boot.support

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.security.TokenAuthenticationToken
import net.nemerosa.ontrack.test.assertIs
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import javax.servlet.FilterChain
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TokenHeaderAuthenticationFilterTest {

    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var filter: TokenHeaderAuthenticationFilter

    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse
    private lateinit var filterChain: FilterChain

    private val token = TokenAuthenticationToken("xxx")
    private val authenticatedToken = TokenAuthenticationToken(
            "xxx",
            AuthorityUtils.createAuthorityList(SecurityRole.USER.roleName),
            mock<UserDetails>()
    )

    @Before
    fun before() {
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
        filterChain = mock()
        authenticationManager = mock()
        filter = TokenHeaderAuthenticationFilter(
                authenticationManager = authenticationManager
        )
        SecurityContextHolder.getContext().authentication = null
    }

    @Test
    fun `Authentication entry point must not be null if we do not ignore the failures`() {
        filter = TokenHeaderAuthenticationFilter(
                authenticationManager = authenticationManager,
                isIgnoreFailure = false
        )
        assertFailsWith<IllegalStateException> {
            filter.afterPropertiesSet()
        }
    }

    @Test
    fun `Authentication entry point can be null if we ignore the failures`() {
        filter = TokenHeaderAuthenticationFilter(
                authenticationManager = authenticationManager,
                isIgnoreFailure = true
        )
        filter.afterPropertiesSet()
    }

    @Test
    fun `No header`() {
        filter.doFilter(request, response, filterChain)
        verify(filterChain).doFilter(request, response)
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `Authentication is required`() {
        request.addHeader("X-Ontrack-Token", "xxx")
        whenever(authenticationManager.authenticate(token)).thenReturn(authenticatedToken)
        filter.doFilter(request, response, filterChain)
        assertNotNull(SecurityContextHolder.getContext().authentication) {
            assertIs<TokenAuthenticationToken>(it) { t ->
                assertTrue(t.matches("xxx"), "Token matches")
            }
        }
    }

}