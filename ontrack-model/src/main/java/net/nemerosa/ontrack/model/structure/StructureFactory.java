package net.nemerosa.ontrack.model.structure;

public interface StructureFactory {

    Project newProject(NameDescription nameDescription);

    Branch newBranch(Project project, NameDescription nameDescription);

    PromotionLevel newPromotionLevel(Branch branch, NameDescription nameDescription);

    ValidationStamp newValidationStamp(Branch branch, NameDescription nameDescription);

    // TODO Properties
    Build newBuild(Branch branch, NameDescription nameDescription);

    // TODO Properties
    PromotionRun newPromotionRun(Build build, PromotionLevel promotionLevel, Signature signature, String description);

    // TODO Properties
    ValidationRun newValidationRun(Build build, ValidationStamp validationStamp, ValidationRunStatusID statusID, Signature signature, String description);


}
