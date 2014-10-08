package net.nemerosa.ontrack.acceptance.browser;

import java.util.Map;

public interface Page {

    String getPath(Map<String, Object> parameters);

    void waitFor();

}
