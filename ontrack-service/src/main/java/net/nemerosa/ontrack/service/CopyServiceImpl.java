package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.security.BranchEdit;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CopyServiceImpl implements CopyService {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;

    @Autowired
    public CopyServiceImpl(StructureService structureService, PropertyService propertyService, SecurityService securityService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
    }

    @Override
    public Branch copy(Branch targetBranch, BranchCopyRequest request) {
        // Gets the source branch
        Branch sourceBranch = structureService.getBranch(request.getSourceBranchId());
        // If same branch, rejects
        if (sourceBranch.id() == targetBranch.id()) {
            throw new IllegalArgumentException("Cannot copy the branch into itself.");
        }
        // Checks the rights on the target branch
        securityService.checkProjectFunction(targetBranch, BranchEdit.class);
        // Now, we can work in a secure context
        securityService.asAdmin(() -> doCopy(sourceBranch, targetBranch, request));
        return targetBranch;
    }

    protected void doCopy(Branch sourceBranch, Branch targetBranch, BranchCopyRequest request) {
        // Same project?
        boolean sameProject = (sourceBranch.projectId() == targetBranch.projectId());
        // TODO Branch properties
        // Promotion level and properties
        doCopyPromotionLevels(sourceBranch, targetBranch, request);
        // TODO Validation stamps and properties
        // TODO User filters
    }

    protected void doCopyPromotionLevels(Branch sourceBranch, Branch targetBranch, BranchCopyRequest request) {
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
                                        applyReplacements(sourcePromotionLevel.getDescription(), request.getPromotionLevelReplacements())
                                )
                        )
                );
                // Copy of the image
                Document image = structureService.getPromotionLevelImage(sourcePromotionLevel.getId());
                if (image != null) {
                    structureService.setPromotionLevelImage(targetPromotionLevel.getId(), image);
                }
                // Copy of properties
                // Gets the properties of the source promotion level
                List<Property<?>> properties = propertyService.getProperties(sourcePromotionLevel);
                for (Property<?> property : properties) {
                    doCopyProperty(property, targetPromotionLevel, request.getPromotionLevelReplacements());
                }
            }
        }
    }

    protected <T> void doCopyProperty(Property<T> property, ProjectEntity targetEntity, List<Replacement> replacements) {
        if (!property.isEmpty()) {
            // Property value replacement
            T data = property.getType().replaceValue(property.getValue(), s -> applyReplacements(s, replacements));
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

    protected static String applyReplacements(final String value, List<Replacement> replacements) {
        String transformedValue = value;
        for (Replacement replacement : replacements) {
            transformedValue = replacement.replace(transformedValue);
        }
        return transformedValue;
    }

}
