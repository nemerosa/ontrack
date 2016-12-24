package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.repository.CoreBuildFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class BuildIntervalFilterProvider extends AbstractBuildFilterProvider<BuildIntervalFilterData> {

    private final CoreBuildFilterRepository filterRepository;

    @Autowired
    public BuildIntervalFilterProvider(CoreBuildFilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    @Override
    public String getType() {
        return BuildIntervalFilterProvider.class.getName();
    }

    @Override
    protected Form fill(Form form, BuildIntervalFilterData data) {
        return form
                .fill("from", data.getFrom())
                .fill("to", data.getTo())
                ;
    }

    @Override
    protected Form blankForm(ID branchId) {
        return Form.create()
                .with(
                        Text.of("from")
                                .label("From build")
                                .help("First build")
                )
                .with(
                        Text.of("to")
                                .label("To build")
                                .optional()
                                .help("Last build")
                );
    }

    @Override
    public String getName() {
        return "Build interval";
    }

    @Override
    public boolean isPredefined() {
        return false;
    }

    @Override
    public List<Build> filterBranchBuilds(Branch branch, BuildIntervalFilterData data) {
        return filterRepository.between(branch, data.getFrom(), data.getTo());
    }

    @Override
    public Optional<BuildIntervalFilterData> parse(JsonNode data) {
        return Optional.of(
                BuildIntervalFilterData.of(
                        JsonUtils.get(data, "from", true, null),
                        JsonUtils.get(data, "to", true, null)
                )
        );
    }
}
