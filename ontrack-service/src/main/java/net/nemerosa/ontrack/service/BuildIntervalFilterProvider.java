package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BuildIntervalFilterProvider extends AbstractBuildFilterProvider<BuildIntervalFilterData> {

    private final StructureService structureService;

    @Autowired
    public BuildIntervalFilterProvider(StructureService structureService) {
        this.structureService = structureService;
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
    public BuildFilter filter(ID branchId, BuildIntervalFilterData data) {
        return new BuildIntervalFilter(structureService, data);
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
