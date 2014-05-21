package net.nemerosa.ontrack.repository.support;

import javax.sql.DataSource;

public abstract class AbstractDBInitConfig implements DBInitConfig {

    protected final DataSource dataSource;

    protected AbstractDBInitConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
