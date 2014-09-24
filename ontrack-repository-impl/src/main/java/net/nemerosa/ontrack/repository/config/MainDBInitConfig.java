package net.nemerosa.ontrack.repository.config;

import net.nemerosa.ontrack.repository.support.AbstractDBInitConfig;
import net.sf.dbinit.DBInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class MainDBInitConfig extends AbstractDBInitConfig {

    public static final int VERSION = 3;

    @Autowired
    public MainDBInitConfig(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String getName() {
        return "main";
    }

    @Override
    public DBInit createConfig() {
        DBInit db = new DBInit();
        db.setVersion(VERSION);
        db.setJdbcDataSource(dataSource);
        db.setVersionTable("ONTRACK_VERSION");
        db.setVersionColumnName("VALUE");
        db.setVersionColumnTimestamp("UPDATED");
        db.setResourceInitialization("/META-INF/db/init.sql");
        db.setResourceUpdate("/META-INF/db/update.{0}.sql");
        return db;
    }

}
