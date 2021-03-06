package net.nemerosa.ontrack.boot.support

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.security.TokenAuthenticationToken
import net.nemerosa.ontrack.model.security.TokenNameMismatchException
import net.nemerosa.ontrack.model.structure.TokensService
import net.nemerosa.ontrack.test.assertIs
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.FilterChain
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TokenHeaderAuthenticationFilterTest {

    private lateinit var tokensService: TokensService
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
        tokensService = mock()
        authenticationManager = mock()
        filter = TokenHeaderAuthenticationFilter(
                authenticationManager = authenticationManager,
                tokensService = tokensService
        )
        SecurityContextHolder.getContext().authentication = null
    }

    @Test
    fun `Authentication entry point must not be null if we do not ignore the failures`() {
        filter = TokenHeaderAuthenticationFilter(
                authenticationManager = authenticationManager,
                tokensService = tokensService,
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
                tokensService = tokensService,
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
    fun `Authentication is required when existing token has been invalidated`() {
        SecurityContextHolder.getContext().authentication = TokenAuthenticationToken("xxx", AuthorityUtils.createAuthorityList(SecurityRole.USER.roleName), mock())
        request.addHeader("X-Ontrack-Token", "xxx")
        whenever(tokensService.isValid("xxx")).thenReturn(true)
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
        whenever(tokensService.isValid("xxx")).thenReturn(true)
        filter.doFilter(request, response, filterChain)
        verify(authenticationManager, never()).authenticate(any())
        assertNotNull(SecurityContextHolder.getContext().authentication) {
            assertIs<TokenAuthenticationToken>(it) { t ->
                assertTrue(t.matches("xxx"), "Token matches")
            }
        }
    }

    @Test
    fun `Authentication failure to ignore`() {
        filter = TokenHeaderAuthenticationFilter(
                authenticationManager = authenticationManager,
                tokensService = tokensService,
                isIgnoreFailure = true
        )
        request.addHeader("X-Ontrack-Token", "xxx")
        whenever(authenticationManager.authenticate(token)).thenThrow(TokenNameMismatchException())
        filter.doFilter(request, response, filterChain)
        assertNull(SecurityContextHolder.getContext().authentication)
        verify(filterChain, times(1)).doFilter(request, response)
    }

    @Test
    fun `Authentication failure not to ignore`() {
        val authenticationEntryPoint = mock<AuthenticationEntryPoint>()
        filter = TokenHeaderAuthenticationFilter(
                authenticationManager = authenticationManager,
                tokensService = tokensService,
                isIgnoreFailure = false,
                authenticationEntryPoint = authenticationEntryPoint
        )
        request.addHeader("X-Ontrack-Token", "xxx")
        val exception = TokenNameMismatchException()
        whenever(authenticationManager.authenticate(token)).thenThrow(exception)
        filter.doFilter(request, response, filterChain)
        assertNull(SecurityContextHolder.getContext().authentication)
        verify(filterChain, never()).doFilter(request, response)
        verify(authenticationEntryPoint, times(1)).commence(request, response, exception)
    }

}