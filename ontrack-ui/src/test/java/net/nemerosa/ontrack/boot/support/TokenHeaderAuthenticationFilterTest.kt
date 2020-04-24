package net.nemerosa.ontrack.boot.support

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.security.TokenAuthenticationToken
import net.nemerosa.ontrack.test.assertIs
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
            mock()
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
    fun `Authentication is required when no existing authentication`() {
        request.addHeader("X-Ontrack-Token", "xxx")
        whenever(authenticationManager.authenticate(token)).thenReturn(authenticatedToken)
        filter.doFilter(request, response, filterChain)
        assertNotNull(SecurityContextHolder.getContext().authentication) {
            assertIs<TokenAuthenticationToken>(it) { t ->
                assertTrue(t.matches("xxx"), "Token matches")
            }
        }
    }

    @Test
    fun `Authentication is required when existing authentication is not authenticated`() {
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken("user", "test")
        request.addHeader("X-Ontrack-Token", "xxx")
        whenever(authenticationManager.authenticate(token)).thenReturn(authenticatedToken)
        filter.doFilter(request, response, filterChain)
        assertNotNull(SecurityContextHolder.getContext().authentication) {
            assertIs<TokenAuthenticationToken>(it) { t ->
                assertTrue(t.matches("xxx"), "Token matches")
            }
        }
    }

    @Test
    fun `Authentication is required when existing token does not match`() {
        SecurityContextHolder.getContext().authentication = TokenAuthenticationToken("yyy", AuthorityUtils.createAuthorityList(SecurityRole.USER.roleName), mock())
        request.addHeader("X-Ontrack-Token", "xxx")
        whenever(authenticationManager.authenticate(token)).thenReturn(authenticatedToken)
        filter.doFilter(request, response, filterChain)
        assertNotNull(SecurityContextHolder.getContext().authentication) {
            assertIs<TokenAuthenticationToken>(it) { t ->
                assertTrue(t.matches("xxx"), "Token matches")
            }
        }
    }

    @Test
    fun `Authentication is not required when existing token matches`() {
        SecurityContextHolder.getContext().authentication = authenticatedToken
        request.addHeader("X-Ontrack-Token", "xxx")
        filter.doFilter(request, response, filterChain)
        verify(authenticationManager, never()).authenticate(any())
        assertNotNull(SecurityContextHolder.getContext().authentication) {
            assertIs<TokenAuthenticationToken>(it) { t ->
                assertTrue(t.matches("xxx"), "Token matches")
            }
        }
    }

}