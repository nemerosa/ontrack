package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.security.BranchEdit;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.function.Function;

import static net.nemerosa.ontrack.model.structure.Replacement.replacementFn;

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
        return copy(targetBranch, sourceBranch, replacementFn, SyncPolicy.COPY);

    }

    @Override
    public Branch copy(Branch targetBranch, Branch sourceBranch, Function<String, String> replacementFn, SyncPolicy syncPolicy) {
        // If same branch, rejects
        if (sourceBranch.id() == targetBranch.id()) {
            throw new CannotCopyItselfException();
        }
        // Checks the rights on the target branch
        securityService.checkProjectFunction(targetBranch, BranchEdit.class);
        // Now, we can work in a secure context
        securityService.asAdmin(() -> doCopy(sourceBranch, targetBranch, replacementFn, syncPolicy));
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
        doCopy(sourceBranch, targetBranch, replacementFn, SyncPolicy.COPY);
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
        doCopyProperties(sourceProject, targetProject, replacementFn, SyncPolicy.COPY);

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
        doCopy(sourceBranch, targetBranch, replacementFn, SyncPolicy.COPY);

        // OK
        return targetProject;
    }

    protected void doCopy(Branch sourceBranch, Branch targetBranch, Function<String, String> replacementFn, SyncPolicy syncPolicy) {
        // Branch properties
        doCopyProperties(sourceBranch, targetBranch, replacementFn, syncPolicy);
        // Promotion level and properties
        doCopyPromotionLevels(sourceBranch, targetBranch, replacementFn, syncPolicy);
        // Validation stamps and properties
        doCopyValidationStamps(sourceBranch, targetBranch, replacementFn, syncPolicy);
        // User filters
        doCopyUserBuildFilters(sourceBranch, targetBranch);
    }

    protected void doCopyUserBuildFilters(Branch sourceBranch, Branch targetBranch) {
        buildFilterService.copyToBranch(sourceBranch.getId(), targetBranch.getId());
    }

    protected void doCopyPromotionLevels(Branch sourceBranch, Branch targetBranch, Function<String, String> replacementFn, SyncPolicy syncPolicy) {
        syncPolicy.sync(
                new SyncConfig<PromotionLevel, String>() {

                    @Override
                    public String getItemType() {
                        return "Promotion level";
                    }

                    @Override
                    public Collection<PromotionLevel> getSourceItems() {
                        return structureService.getPromotionLevelListForBranch(sourceBranch.getId());
                    }

                    @Override
                    public Collection<PromotionLevel> getTargetItems() {
                        return structureService.getPromotionLevelListForBranch(targetBranch.getId());
                    }

                    @Override
                    public String getItemId(PromotionLevel item) {
                        return item.getName();
                    }

                    @Override
                    public void createTargetItem(PromotionLevel sourcePromotionLevel) {
                        PromotionLevel targetPromotionLevel = structureService.newPromotionLevel(
                                PromotionLevel.of(
                                        targetBranch,
                                        NameDescription.nd(
                                                sourcePromotionLevel.getName(),
                                                replacementFn.apply(sourcePromotionLevel.getDescription())
                                        )
                                )
                        );
                        copyPromotionLevelContent(sourcePromotionLevel, targetPromotionLevel);

                    }

                    @Override
                    public void replaceTargetItem(PromotionLevel sourcePromotionLevel, PromotionLevel targetPromotionLevel) {
                        structureService.savePromotionLevel(
                                targetPromotionLevel.withDescription(replacementFn.apply(sourcePromotionLevel.getDescription()))
                        );
                        copyPromotionLevelContent(sourcePromotionLevel, targetPromotionLevel);
                    }

                    @Override
                    public void deleteTargetItem(PromotionLevel target) {
                        structureService.deletePromotionLevel(target.getId());
                    }

                    private void copyPromotionLevelContent(PromotionLevel sourcePromotionLevel, PromotionLevel targetPromotionLevel) {
                        // Copy of the image
                        Document image = structureService.getPromotionLevelImage(sourcePromotionLevel.getId());
                        if (image != null) {
                            structureService.setPromotionLevelImage(targetPromotionLevel.getId(), image);
                        }
                        // Copy of properties
                        doCopyProperties(sourcePromotionLevel, targetPromotionLevel, replacementFn, syncPolicy);
                    }
                }
        );
    }

    protected void doCopyProperties(ProjectEntity source, ProjectEntity target, Function<String, String> replacementFn, SyncPolicy syncPolicy) {
        syncPolicy.sync(
                new SyncConfig<Property<?>, String>() {

                    @Override
                    public String getItemType() {
                        return "Property";
                    }

                    @Override
                    public Collection<Property<?>> getSourceItems() {
                        return propertyService.getProperties(source);
                    }

                    @Override
                    public Collection<Property<?>> getTargetItems() {
                        return propertyService.getProperties(target);
                    }

                    @Override
                    public String getItemId(Property<?> item) {
                        return item.getType().getTypeName();
                    }

                    @Override
                    public void createTargetItem(Property<?> sourceProperty) {
                        doCopyProperty(sourceProperty, target, replacementFn);
                    }

                    @Override
                    public void replaceTargetItem(Property<?> sourceProperty, Property<?> targetProperty) {
                        doCopyProperty(sourceProperty, target, replacementFn);
                    }

                    @Override
                    public void deleteTargetItem(Property<?> targetProperty) {
                        propertyService.deleteProperty(target, targetProperty.getType().getTypeName());
                    }
                }
        );
    }

    protected void doCopyValidationStamps(Branch sourceBranch, Branch targetBranch, Function<String, String> replacementFn, SyncPolicy syncPolicy) {
        syncPolicy.sync(
                new SyncConfig<ValidationStamp, String>() {
                    @Override
                    public String getItemType() {
                        return "Validation stamp";
                    }

                    @Override
                    public Collection<ValidationStamp> getSourceItems() {
                        return structureService.getValidationStampListForBranch(sourceBranch.getId());
                    }

                    @Override
                    public Collection<ValidationStamp> getTargetItems() {
                        return structureService.getValidationStampListForBranch(targetBranch.getId());
                    }

                    @Override
                    public String getItemId(ValidationStamp item) {
                        return item.getName();
                    }

                    @Override
                    public void createTargetItem(ValidationStamp sourceValidationStamp) {
                        ValidationStamp targetValidationStamp = structureService.newValidationStamp(
                                ValidationStamp.of(
                                        targetBranch,
                                        NameDescription.nd(
                                                sourceValidationStamp.getName(),
                                                replacementFn.apply(sourceValidationStamp.getDescription())
                                        )
                                )
                        );
                        copyValidationStampContent(sourceValidationStamp, targetValidationStamp);

                    }

                    @Override
                    public void replaceTargetItem(ValidationStamp sourceValidationStamp, ValidationStamp targetValidationStamp) {
                        structureService.saveValidationStamp(
                                targetValidationStamp.withDescription(
                                        replacementFn.apply(sourceValidationStamp.getDescription())
                                )
                        );
                        copyValidationStampContent(sourceValidationStamp, targetValidationStamp);
                    }

                    @Override
                    public void deleteTargetItem(ValidationStamp target) {
                        structureService.deleteValidationStamp(target.getId());
                    }

                    private void copyValidationStampContent(ValidationStamp sourceValidationStamp, ValidationStamp targetValidationStamp) {
                        // Copy of the image
                        Document image = structureService.getValidationStampImage(sourceValidationStamp.getId());
                        if (image != null) {
                            structureService.setValidationStampImage(targetValidationStamp.getId(), image);
                        }
                        // Copy of properties
                        doCopyProperties(sourceValidationStamp, targetValidationStamp, replacementFn, syncPolicy);
                    }
                }
        );
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

}
