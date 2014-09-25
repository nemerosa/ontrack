package net.nemerosa.ontrack.repository.support;

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
    ConfiguredDBInit createConfig();

    /**
     * Order of execution
     */
    int getOrder();

}
