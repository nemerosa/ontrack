package net.nemerosa.ontrack.repository.support;

import net.sf.dbinit.DBInit;

/**
 * Declaration for a database initialisation.
 */
public interface DBInitConfig {

    /**
     * Display name
     */
    String getName();

    /**
     * <code>DBInit</code> configuration.
     *
     * @see net.sf.dbinit.DBInit
     */
    DBInit createConfig();

    /**
     * Order of execution
     */
    int getOrder();

}
