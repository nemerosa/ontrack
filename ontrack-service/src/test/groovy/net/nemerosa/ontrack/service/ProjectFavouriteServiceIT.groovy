package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.structure.ProjectFavouriteService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ProjectFavouriteServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private ProjectFavouriteService service

    @Test
    void 'Anonymous cannot set favourites'() {
        def project = doCreateProject()
        assert !service.isProjectFavourite(project)
        service.setProjectFavourite(project, true)
        assert !service.isProjectFavourite(project)
    }

    @Test
    void 'Setting and unsetting a project as favourite'() {
        def project = doCreateProject()
        def account = doCreateAccount()
        asAccount(account).withView(project).call {
            assert !service.isProjectFavourite(project)
            service.setProjectFavourite(project, true)
            assert service.isProjectFavourite(project)
            service.setProjectFavourite(project, false)
            assert !service.isProjectFavourite(project)
        }
    }

}
