package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.TemplateDefinition;
import net.nemerosa.ontrack.model.structure.TemplateParameter;
import net.nemerosa.ontrack.model.structure.TemplateSynchronisationAbsencePolicy;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BranchTemplateJdbcRepository extends AbstractJdbcRepository implements BranchTemplateRepository {

    @Autowired
    public BranchTemplateJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<TemplateDefinition> getTemplateDefinition(ID branchId) {
        return getOptional(
                "SELECT * FROM BRANCH_TEMPLATE_DEFINITIONS WHERE BRANCHID = :branchId",
                params("branchId", branchId.get()),
                (rs, num) -> toTemplateDefinition(rs)
        );
    }

    private TemplateDefinition toTemplateDefinition(ResultSet rs) throws SQLException {
        int branchId = rs.getInt("branchId");
        // Gets the parameters
        List<TemplateParameter> parameters = getNamedParameterJdbcTemplate().query(
                "SELECT * FROM BRANCH_TEMPLATE_DEFINITION_PARAMS WHERE BRANCHID = :branchId ORDER BY NAME",
                params("branchId", branchId),
                (rsp, num) -> new TemplateParameter(
                        rsp.getString("NAME"),
                        rsp.getString("DESCRIPTION"),
                        rsp.getString("EXPRESSION")
                )
        );
        // OK
        return new TemplateDefinition(
                parameters,
                rs.getString("SYNCHRONISATIONSOURCEID"),
                readJson(rs, "SYNCHRONISATIONSOURCECONFIG"),
                getEnum(TemplateSynchronisationAbsencePolicy.class, rs, "ABSENCEPOLICY"),
                rs.getInt("SYNCINTERVAL")
        );
    }

    @Override
    public void setTemplateDefinition(ID branchId, TemplateDefinition templateDefinition) {
        // Deletes previous value
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM BRANCH_TEMPLATE_DEFINITIONS WHERE BRANCHID = :branchId",
                params("branchId", branchId.get())
        );
        // Definition
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO BRANCH_TEMPLATE_DEFINITIONS(BRANCHID, ABSENCEPOLICY, SYNCINTERVAL, SYNCHRONISATIONSOURCEID, SYNCHRONISATIONSOURCECONFIG) " +
                        "VALUES (:branchId, :absencePolicy, :interval, :synchronisationSourceId, :synchronisationSourceConfig)",
                params("branchId", branchId.get())
                        .addValue("absencePolicy", templateDefinition.getAbsencePolicy().name())
                        .addValue("interval", templateDefinition.getInterval())
                        .addValue("synchronisationSourceId", templateDefinition.getSynchronisationSourceId())
                        .addValue("synchronisationSourceConfig", writeJson(templateDefinition.getSynchronisationSourceConfig()))
        );
        // Parameters
        for (TemplateParameter parameter : templateDefinition.getParameters()) {
            getNamedParameterJdbcTemplate().update(
                    "INSERT INTO BRANCH_TEMPLATE_DEFINITION_PARAMS(BRANCHID, NAME, DESCRIPTION, EXPRESSION) " +
                            "VALUES (:branchId, :name, :description, :expression)",
                    params("branchId", branchId.get())
                            .addValue("name", parameter.getName())
                            .addValue("description", parameter.getDescription())
                            .addValue("expression", parameter.getExpression())
            );
        }
    }

    @Override
    public boolean isTemplateDefinition(ID branchId) {
        return getFirstItem(
                "SELECT BRANCHID FROM BRANCH_TEMPLATE_DEFINITIONS WHERE BRANCHID = :branchId",
                params("branchId", branchId.get()),
                Integer.class
        ) != null;
    }

    @Override
    public boolean isTemplateInstance(ID branchId) {
        // FIXME Method net.nemerosa.ontrack.repository.BranchTemplateJdbcRepository.isTemplateInstance
        return false;
    }
}
