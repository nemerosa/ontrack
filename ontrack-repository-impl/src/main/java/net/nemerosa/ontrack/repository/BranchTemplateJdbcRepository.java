package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class BranchTemplateJdbcRepository extends AbstractJdbcRepository implements BranchTemplateRepository {

    @Autowired
    public BranchTemplateJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Collection<BranchTemplateDefinition> getTemplateDefinitions() {
        return getJdbcTemplate().query(
                "SELECT * FROM BRANCH_TEMPLATE_DEFINITIONS",
                (rs, num) -> new BranchTemplateDefinition(
                        id(rs, "BRANCHID"),
                        toTemplateDefinition(rs)
                )
        );
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
                new ServiceConfiguration(
                        rs.getString("SYNCHRONISATIONSOURCEID"),
                        readJson(rs, "SYNCHRONISATIONSOURCECONFIG")
                ),
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
                        "VALUES (:branchId, :absencePolicy, :interval, :synchronisationSourceId, CAST(:synchronisationSourceConfig AS JSONB))",
                params("branchId", branchId.get())
                        .addValue("absencePolicy", templateDefinition.getAbsencePolicy().name())
                        .addValue("interval", templateDefinition.getInterval())
                        .addValue("synchronisationSourceId", templateDefinition.getSynchronisationSourceConfig().getId())
                        .addValue("synchronisationSourceConfig", writeJson(templateDefinition.getSynchronisationSourceConfig().getData()))
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
    public Optional<TemplateInstance> getTemplateInstance(ID branchId) {
        return getOptional(
                "SELECT * FROM BRANCH_TEMPLATE_INSTANCES WHERE BRANCHID = :branchId",
                params("branchId", branchId.get()),
                (rs, num) -> toTemplateInstance(rs)
        );
    }

    private TemplateInstance toTemplateInstance(ResultSet rs) throws SQLException {
        int branchId = rs.getInt("branchId");
        // Gets the parameters
        List<TemplateParameterValue> parameters = getNamedParameterJdbcTemplate().query(
                "SELECT * FROM BRANCH_TEMPLATE_INSTANCE_PARAMS WHERE BRANCHID = :branchId ORDER BY NAME",
                params("branchId", branchId),
                (rsp, num) -> new TemplateParameterValue(
                        rsp.getString("NAME"),
                        rsp.getString("VALUE")
                )
        );
        // OK
        return new TemplateInstance(
                id(rs, "TEMPLATEBRANCHID"),
                parameters
        );
    }

    @Override
    public Collection<BranchTemplateInstance> getTemplateInstancesForDefinition(ID templateDefinitionId) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM BRANCH_TEMPLATE_INSTANCES WHERE TEMPLATEBRANCHID = :templateDefinitionId",
                params("templateDefinitionId", templateDefinitionId.get()),
                (rs, num) -> new BranchTemplateInstance(
                        id(rs, "BRANCHID"),
                        toTemplateInstance(rs)
                )
        );
    }

    @Override
    public void disconnectTemplateInstance(ID branchId) {
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM BRANCH_TEMPLATE_INSTANCES WHERE BRANCHID = :branchId",
                params("branchId", branchId.get())
        );
    }

    @Override
    public void setTemplateInstance(ID branchId, TemplateInstance templateInstance) {
        // Deletes previous value
        disconnectTemplateInstance(branchId);
        // Definition
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO BRANCH_TEMPLATE_INSTANCES(BRANCHID, TEMPLATEBRANCHID) " +
                        "VALUES (:branchId, :templateBranchId)",
                params("branchId", branchId.get())
                        .addValue("templateBranchId", templateInstance.getTemplateDefinitionId().get())
        );
        // Parameters
        for (TemplateParameterValue parameter : templateInstance.getParameterValues()) {
            getNamedParameterJdbcTemplate().update(
                    "INSERT INTO BRANCH_TEMPLATE_INSTANCE_PARAMS(BRANCHID, NAME, VALUE) " +
                            "VALUES (:branchId, :name, :value)",
                    params("branchId", branchId.get())
                            .addValue("name", parameter.getName())
                            .addValue("value", parameter.getValue())
            );
        }
    }

    @Override
    public boolean isTemplateInstance(ID branchId) {
        return getFirstItem(
                "SELECT BRANCHID FROM BRANCH_TEMPLATE_INSTANCES WHERE BRANCHID = :branchId",
                params("branchId", branchId.get()),
                Integer.class
        ) != null;
    }
}
