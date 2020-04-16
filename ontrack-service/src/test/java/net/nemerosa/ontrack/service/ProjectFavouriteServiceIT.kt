package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.ProjectFavouriteService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProjectFavouriteServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var service: ProjectFavouriteService

    @Test
    fun `Anonymous cannot set favourites`() {
        val project = project()
        asAnonymous {
            assertFalse(service.isProjectFavourite(project))
            service.setProjectFavourite(project, true)
            assertFalse(service.isProjectFavourite(project))
        }
    }

    @Test
    fun `Setting and unsetting a project as favourite`() {
        val project = project()
        val account = doCreateAccount()
        asAccount(account).withView(project).call {
            assertFalse(service.isProjectFavourite(project))
            service.setProjectFavourite(project, true)
            assertTrue(service.isProjectFavourite(project))
            service.setProjectFavourite(project, false)
            assertFalse(service.isProjectFavourite(project))
        }
    }

}
