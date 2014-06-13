package net.nemerosa.ontrack.model.support;

import java.util.List;

/**
 * This interface defines a component that is able to return a list of messages about the state
 * of the application.
 */
public interface ApplicationInfoProvider {

    List<ApplicationInfo> getApplicationInfoList();

}
