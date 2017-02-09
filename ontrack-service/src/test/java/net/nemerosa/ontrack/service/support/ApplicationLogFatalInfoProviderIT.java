package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.ApplicationManagement;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoType;
import net.nemerosa.ontrack.model.support.ApplicationLogEntry;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApplicationLogFatalInfoProviderIT extends AbstractServiceTestSupport {

    @Autowired
    private ApplicationLogService logService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ApplicationLogFatalInfoProvider provider;

    /**
     * Makes sure to have some fatal errors
     */
    @Before
    public void fatal_errors() {
        logService.log(
                ApplicationLogEntry.fatal(
                        new RuntimeException("Oops"),
                        NameDescription.nd("fatal", "Should be investigated"),
                        "Something went wrong"
                )
        );
    }

    @Test
    public void fatal_errors_when_admin() throws Exception {
        List<ApplicationInfo> list = asUser().with(ApplicationManagement.class).call(() -> provider.getApplicationInfoList());
        assertTrue("At least one fatal error to display", list.size() >= 1);
        ApplicationInfo info = list.get(0);
        assertEquals(ApplicationInfoType.ERROR, info.getType());
    }

    @Test
    public void no_fatal_errors_when_not_admin() throws Exception {
        List<ApplicationInfo> list = asUser().call(() -> provider.getApplicationInfoList());
        assertTrue("No fatal error to display", list.isEmpty());
    }

}
