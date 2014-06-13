package net.nemerosa.ontrack.model.support;

import java.util.List;

/**
 * This service returns the list of application info messages, as returned by all the
 * registered {@link net.nemerosa.ontrack.model.support.ApplicationInfoProvider}.
 */
public interface ApplicationInfoService {

    List<ApplicationInfo> getApplicationInfoList();

}
