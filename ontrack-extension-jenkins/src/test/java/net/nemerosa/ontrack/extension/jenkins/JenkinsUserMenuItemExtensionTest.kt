package net.nemerosa.ontrack.extension.jenkins

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JenkinsUserMenuItemExtensionTest {

    private lateinit var jenkinsUserMenuItemExtension: JenkinsUserMenuItemExtension
    private lateinit var securityService: SecurityService

    @BeforeEach
    fun init() {
        val jenkinsExtensionFeature = mockk<JenkinsExtensionFeature>()
        every { jenkinsExtensionFeature.id } returns "jenkins"

        securityService = mockk<SecurityService>()

        jenkinsUserMenuItemExtension = JenkinsUserMenuItemExtension(
            jenkinsExtensionFeature = jenkinsExtensionFeature,
            securityService = securityService,
        )
    }

    @Test
    fun `No configuration menu if global settings function is not granted`() {
        every { securityService.isGlobalFunctionGranted(GlobalSettings::class.java) } returns false
        val items = jenkinsUserMenuItemExtension.items
        assertEquals(emptyList(), items, "No menu item")
    }

    @Test
    fun `Configuration menu is present if global settings function is granted`() {
        every { securityService.isGlobalFunctionGranted(GlobalSettings::class.java) } returns true
        val items = jenkinsUserMenuItemExtension.items
        assertEquals(
            listOf(
                UserMenuItem(
                    groupId = CoreUserMenuGroups.CONFIGURATIONS,
                    extension = "extension/jenkins",
                    id = "configurations",
                    name = "Jenkins configurations",
                )
            ),
            items,
            "Configuration menu item"
        )
    }

}