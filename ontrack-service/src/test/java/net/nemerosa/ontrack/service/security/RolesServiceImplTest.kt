package net.nemerosa.ontrack.service.security

import io.mockk.mockk
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.test.assertPresent
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RolesServiceImplTest {

    private lateinit var rolesService: RolesServiceImpl

    @BeforeEach
    fun init() {
        rolesService = RolesServiceImpl(
            roleContributors = emptyList(),
            cachedSettingsService = mockk(),
        )
        rolesService.start()
    }

    @Test
    fun getGlobalRole_administrator() {
        assertTrue(rolesService.getGlobalRole("ADMINISTRATOR").isPresent)
    }

    @Test
    fun getGlobalRole_controller() {
        assertTrue(rolesService.getGlobalRole("CONTROLLER").isPresent)
    }

    @Test
    fun getGlobalRole_unknown() {
        assertFalse(rolesService.getGlobalRole("XXX").isPresent)
    }

    @Test
    fun getProjectRole_owner() {
        val owner = rolesService.getProjectRole("OWNER")
        assertTrue(owner.isPresent)
        assertTrue(owner.get().functions.contains(ProjectEdit::class.java))
        assertFalse(owner.get().functions.contains(ProjectDelete::class.java))
    }

    @Test
    fun getProjectRole_participant() {
        val participant = rolesService.getProjectRole("PARTICIPANT")
        assertPresent(participant) {
            assertTrue(it.functions.contains(ValidationStampFilterCreate::class.java))
            assertFalse(it.functions.contains(ValidationStampFilterShare::class.java))
            assertFalse(it.functions.contains(ValidationStampFilterMgt::class.java))
        }
    }

    @Test
    fun getProjectRole_validation_stamp_manager() {
        val manager = rolesService.getProjectRole("VALIDATION_MANAGER")
        assertPresent(manager) {
            assertTrue(it.functions.contains(ValidationStampFilterCreate::class.java))
            assertTrue(it.functions.contains(ValidationStampFilterShare::class.java))
            assertTrue(it.functions.contains(ValidationStampFilterMgt::class.java))
        }
    }

    @Test
    fun getProjectRole_project_manager() {
        val manager = rolesService.getProjectRole("PROJECT_MANAGER")
        assertPresent(manager) {
            assertTrue(it.functions.contains(ValidationStampFilterCreate::class.java))
            assertTrue(it.functions.contains(ValidationStampFilterShare::class.java))
            assertTrue(it.functions.contains(ValidationStampFilterMgt::class.java))
        }
    }

}