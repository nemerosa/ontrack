package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.CoreBuildFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NamedBuildFilterProvider extends AbstractBuildFilterProvider<NamedBuildFilterData> {

    private final StructureService structureService;
    private final CoreBuildFilterRepository filterRepository;

    @Autowired
    public NamedBuildFilterProvider(StructureService structureService, CoreBuildFilterRepository filterRepository) {
        this.structureService = structureService;
        this.filterRepository = filterRepository;
    }

    @Override
    public String getType() {
        return NamedBuildFilterProvider.class.getName();
    }

    @Override
    public String getName() {
        return "Name filter";
    }

    @Override
    public boolean isPredefined() {
        return false;
    }

    @Override
    protected Form blankForm(ID branchId) {
        // Promotion levels for this branch
        List<PromotionLevel> promotionLevels = structureService.getPromotionLevelListForBranch(branchId);
        // Form
        return Form.create()
                .with(
                        Int.of("count")
                                .label("Maximum count")
                                .help("Maximum number of builds to display")
                                .min(1)
                                .value(10)
                )
                .with(
                        Text.of("fromBuild")
                                .label("From build")
                                .help("Expression to identify a list of build. Only the most recent one is kept. " +
                                        "* (star) can be used as a placeholder.")
                )
                .with(
                        Text.of("toBuild")
                                .label("To build")
                                .optional()
                                .help("Optional expression to identify a list of build. Only the most recent one is kept. " +
                                        "If unset, the first build that does not comply with the \"from build\" expression is kept by default. " +
                                        "* (star) can be used as a placeholder.")
                )
                .with(
                        Selection.of("withPromotionLevel")
                                .label("With promotion level")
                                .help("Optional. If set, restrict both \"from\" and \"to\" list to the builds with a promotion level.")
                                .items(promotionLevels)
                                .itemId("name")
                                .optional()
                )
                ;
    }

    @Override
    protected Form fill(Form form, NamedBuildFilterData data) {
        return form
                .fill("count", data.getCount())
                .fill("fromBuild", data.getFromBuild())
                .fill("toBuild", data.getToBuild())
                .fill("withPromotionLevel", data.getWithPromotionLevel())
                ;
    }

    @Override
    public Optional<NamedBuildFilterData> parse(JsonNode data) {
        NamedBuildFilterData filter = NamedBuildFilterData.of(JsonUtils.get(data, "fromBuild", ""))
                .withCount(JsonUtils.getInt(data, "count", 10))
                .withToBuild(JsonUtils.get(data, "toBuild", null))
                .withWithPromotionLevel(JsonUtils.get(data, "withPromotionLevel", null));
        return Optional.of(filter);
    }

    @Override
    public List<Build> filterBranchBuilds(Branch branch, NamedBuildFilterData data) {
        return filterRepository.nameFilter(
                branch,
                data.getFromBuild(),
                data.getToBuild(),
                data.getWithPromotionLevel(),
                data.getCount()
        );
    }
}
