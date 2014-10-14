package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.security.BranchEdit;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class CopyServiceImpl implements CopyService {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;
    private final BuildFilterService buildFilterService;

    @Autowired
    public CopyServiceImpl(StructureService structureService, PropertyService propertyService, SecurityService securityService, BuildFilterService buildFilterService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
        this.buildFilterService = buildFilterService;
    }

    @Override
    public Branch copy(Branch targetBranch, BranchCopyRequest request) {
        // Replacement function
        Function<String, String> replacementFn = replacementFn(request.getReplacements());
        // Gets the source branch
        Branch sourceBranch = structureService.getBranch(request.getSourceBranchId());
        // Actual copy
        return copy(targetBranch, sourceBranch, replacementFn);

    }

    @Override
    public Branch copy(Branch targetBranch, Branch sourceBranch, Function<String, String> replacementFn) {
        // If same branch, rejects
        if (sourceBranch.id() == targetBranch.id()) {
            throw new CannotCopyItselfException();
        }
        // Checks the rights on the target branch
        securityService.checkProjectFunction(targetBranch, BranchEdit.class);
        // Now, we can work in a secure context
        securityService.asAdmin(() -> doCopy(sourceBranch, targetBranch, replacementFn));
        // OK
        return targetBranch;
    }

    @Override
    public Branch cloneBranch(Branch sourceBranch, BranchCloneRequest request) {
        // Replacement function
        Function<String, String> replacementFn = replacementFn(request.getReplacements());
        // Description of the target branch
        String targetDescription = replacementFn.apply(sourceBranch.getDescription());
        // Creates the branch
        Branch targetBranch = structureService.newBranch(
                Branch.of(
                        sourceBranch.getProject(),
                        NameDescription.nd(request.getName(), targetDescription)
                )
        );
        // Copies the configuration
        doCopy(sourceBranch, targetBranch, replacementFn);
        // OK
        return targetBranch;
    }

    @Override
    public Project cloneProject(Project sourceProject, ProjectCloneRequest request) {

        // Replacement function
        Function<String, String> replacementFn = replacementFn(request.getReplacements());

        // Description of the target project
        String targetProjectDescription = replacementFn.apply(sourceProject.getDescription());

        // Creates the project
        Project targetProject = structureService.newProject(
                Project.of(
                        NameDescription.nd(request.getName(), targetProjectDescription)
                )
        );

        // Copies the properties for the project
        doCopyProperties(sourceProject, targetProject, replacementFn);

        // Creates a copy of the branch
        Branch sourceBranch = structureService.getBranch(request.getSourceBranchId());
        String targetBranchName = replacementFn.apply(sourceBranch.getName());
        String targetBranchDescription = replacementFn.apply(sourceBranch.getDescription());
        Branch targetBranch = structureService.newBranch(
                Branch.of(
                        targetProject,
                        NameDescription.nd(targetBranchName, targetBranchDescription)
                )
        );

        // Configuration of the new branch
        doCopy(sourceBranch, targetBranch, replacementFn);

        // OK
        return targetProject;
    }

    protected void doCopy(Branch sourceBranch, Branch targetBranch, Function<String, String> replacementFn) {
        // Branch properties
        doCopyProperties(sourceBranch, targetBranch, replacementFn);
        // Promotion level and properties
        doCopyPromotionLevels(sourceBranch, targetBranch, replacementFn);
        // Validation stamps and properties
        doCopyValidationStamps(sourceBranch, targetBranch, replacementFn);
        // User filters
        doCopyUserBuildFilters(sourceBranch, targetBranch);
    }

    protected void doCopyUserBuildFilters(Branch sourceBranch, Branch targetBranch) {
        buildFilterService.copyToBranch(sourceBranch.getId(), targetBranch.getId());
    }

    protected void doCopyPromotionLevels(Branch sourceBranch, Branch targetBranch, Function<String, String> replacementFn) {
        List<PromotionLevel> sourcePromotionLevels = structureService.getPromotionLevelListForBranch(sourceBranch.getId());
        for (PromotionLevel sourcePromotionLevel : sourcePromotionLevels) {
            Optional<PromotionLevel> targetPromotionLevelOpt = structureService.findPromotionLevelByName(targetBranch.getProject().getName(), targetBranch.getName(), sourcePromotionLevel.getName());
            if (!targetPromotionLevelOpt.isPresent()) {
                // Copy of the promotion level
                PromotionLevel targetPromotionLevel = structureService.newPromotionLevel(
                        PromotionLevel.of(
                                targetBranch,
                                NameDescription.nd(
                                        sourcePromotionLevel.getName(),
                                        replacementFn.apply(sourcePromotionLevel.getDescription())
                                )
                        )
                );
                // Copy of the image
                Document image = structureService.getPromotionLevelImage(sourcePromotionLevel.getId());
                if (image != null) {
                    structureService.setPromotionLevelImage(targetPromotionLevel.getId(), image);
                }
                // Copy of properties
                doCopyProperties(sourcePromotionLevel, targetPromotionLevel, replacementFn);
            }
        }
    }

    protected void doCopyProperties(ProjectEntity source, ProjectEntity target, Function<String, String> replacementFn) {
        List<Property<?>> properties = propertyService.getProperties(source);
        for (Property<?> property : properties) {
            doCopyProperty(property, target, replacementFn);
        }
    }

    protected void doCopyValidationStamps(Branch sourceBranch, Branch targetBranch, Function<String, String> replacementFn) {
        List<ValidationStamp> sourceValidationStamps = structureService.getValidationStampListForBranch(sourceBranch.getId());
        for (ValidationStamp sourceValidationStamp : sourceValidationStamps) {
            Optional<ValidationStamp> targetValidationStampOpt = structureService.findValidationStampByName(targetBranch.getProject().getName(), targetBranch.getName(), sourceValidationStamp.getName());
            if (!targetValidationStampOpt.isPresent()) {
                // Copy of the validation stamp
                ValidationStamp targetValidationStamp = structureService.newValidationStamp(
                        ValidationStamp.of(
                                targetBranch,
                                NameDescription.nd(
                                        sourceValidationStamp.getName(),
                                        replacementFn.apply(sourceValidationStamp.getDescription())
                                )
                        )
                );
                // Copy of the image
                Document image = structureService.getValidationStampImage(sourceValidationStamp.getId());
                if (image != null) {
                    structureService.setValidationStampImage(targetValidationStamp.getId(), image);
                }
                // Copy of properties
                doCopyProperties(sourceValidationStamp, targetValidationStamp, replacementFn);
            }
        }
    }

    protected <T> void doCopyProperty(Property<T> property, ProjectEntity targetEntity, Function<String, String> replacementFn) {
        if (!property.isEmpty() && property.getType().canEdit(targetEntity, securityService)) {
            // Property value replacement
            T data = property.getType().replaceValue(property.getValue(), replacementFn);
            // Property data
            JsonNode jsonData = property.getType().forStorage(data);
            // Creates the property
            propertyService.editProperty(
                    targetEntity,
                    property.getType().getTypeName(),
                    jsonData
            );
        }
    }

    protected static Function<String, String> replacementFn(List<Replacement> replacements) {
        return (String value) -> {
            String transformedValue = value;
            for (Replacement replacement : replacements) {
                transformedValue = replacement.replace(transformedValue);
            }
            return transformedValue;
        };
    }

}
