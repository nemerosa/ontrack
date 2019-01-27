package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.BranchFavouriteService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BranchFavouriteServiceIT: AbstractDSLTestSupport() {

    @Autowired
    private lateinit var service: BranchFavouriteService

    @Test
    fun `Anonymous cannot set favourites`() {
        project {
            branch {
                assertFalse(service.isBranchFavourite(this), "Branch not favorite by default")
                // Sets as favourite
                asAnonymous().execute {
                    service.setBranchFavourite(this, true)
                }
                assertFalse(service.isBranchFavourite(this), "Branch not set as favorite")
            }
        }
    }

    @Test
    fun `Setting and unsetting a project as favourite`() {
        val account = doCreateAccount()
        project {
            branch {
                asAccount(account).withView(this).execute {
                    // Sets as favourite
                    service.setBranchFavourite(this, true)
                    assertTrue(service.isBranchFavourite(this), "Branch is a favorite")
                    // Unsets as favorite
                    service.setBranchFavourite(this, false)
                    assertFalse(service.isBranchFavourite(this), "Branch not set as favorite")
                }
            }
        }
    }

}
