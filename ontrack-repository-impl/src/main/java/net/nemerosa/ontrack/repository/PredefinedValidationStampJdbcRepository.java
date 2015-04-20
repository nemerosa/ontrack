package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PredefinedValidationStampJdbcRepository extends AbstractJdbcRepository implements PredefinedValidationStampRepository {

    @Autowired
    public PredefinedValidationStampJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<PredefinedValidationStamp> getPredefinedValidationStamps() {
        return getJdbcTemplate().query(
                "SELECT * FROM PREDEFINED_VALIDATION_STAMPS ORDER BY ORDERNB",
                (rs, rowNum) -> toPredefinedValidationStamp(rs)
        );
    }

    protected PredefinedValidationStamp toPredefinedValidationStamp(ResultSet rs) throws SQLException {
        return PredefinedValidationStamp.of(
                new NameDescription(
                        rs.getString("name"),
                        rs.getString("description")
                )
        ).withId(id(rs)).withImage(StringUtils.isNotBlank(rs.getString("imagetype")));
    }
}
