package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.exceptions.*;
import net.nemerosa.ontrack.model.job.*;
import net.nemerosa.ontrack.model.security.BranchTemplateMgt;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.BranchTemplateRepository;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Transactional
public class BranchTemplateServiceImpl implements BranchTemplateService, JobProvider {

    private final Logger logger = LoggerFactory.getLogger(BranchTemplateService.class);

    private final StructureService structureService;
    private final SecurityService securityService;
    private final BranchTemplateRepository branchTemplateRepository;
    private final ExpressionEngine expressionEngine;
    private final CopyService copyService;
    private final EventPostService eventPostService;
    private final EventFactory eventFactory;
    private final TemplateSynchronisationService templateSynchronisationService;

    @Autowired
    public BranchTemplateServiceImpl(StructureService structureService, SecurityService securityService, BranchTemplateRepository branchTemplateRepository, ExpressionEngine expressionEngine, CopyService copyService, EventPostService eventPostService, EventFactory eventFactory, TemplateSynchronisationService templateSynchronisationService) {
        this.structureService = structureService;
        this.securityService = securityService;
        this.branchTemplateRepository = branchTemplateRepository;
        this.expressionEngine = expressionEngine;
        this.copyService = copyService;
        this.eventPostService = eventPostService;
        this.eventFactory = eventFactory;
        this.templateSynchronisationService = templateSynchronisationService;
    }

    @Override
    public Optional<TemplateDefinition> getTemplateDefinition(ID branchId) {
        return branchTemplateRepository.getTemplateDefinition(branchId);
    }

    @Override
    public Branch setTemplateDefinition(ID branchId, TemplateDefinition templateDefinition) {
        // Loads the branch
        Branch branch = structureService.getBranch(branchId);
        // Checks the rights
        securityService.checkProjectFunction(branch, BranchTemplateMgt.class);
        // Checks the branch is NOT an instance
        if (branch.getType() == BranchType.TEMPLATE_INSTANCE) {
            throw new BranchTemplateInstanceException(branch.getName());
        }
        // Checks the builds
        int buildCount = structureService.getBuildCount(branch);
        if (buildCount > 0) {
            throw new BranchTemplateHasBuildException(branch.getName());
        }
        // TODO In case of updates, checks for impact on the instances
        // Saves the definition
        branchTemplateRepository.setTemplateDefinition(branchId, templateDefinition);
        // Reloads the branch
        return structureService.getBranch(branchId);
    }

    @Override
    public Branch createTemplateInstance(ID branchId, BranchTemplateInstanceSingleRequest request) {
        // Loads the branch template definition
        Branch branch = structureService.getBranch(branchId);
        // Gets the template definition
        TemplateDefinition templateDefinition = branchTemplateRepository.getTemplateDefinition(branchId)
                .orElseThrow(() -> new BranchNotTemplateDefinitionException(branchId));
        // Checks the rights
        securityService.checkProjectFunction(branch, BranchTemplateMgt.class);

        // Gets the existing branch
        String sourceName = request.getName();
        String branchName = NameDescription.escapeName(sourceName);
        Optional<Branch> existingBranch = structureService.findBranchByName(
                branch.getProject().getName(),
                sourceName);

        // Not existing
        if (!existingBranch.isPresent()) {
            // Creates the branch
            Branch instance = createBranchForTemplateInstance(branch, sourceName);
            // Updates the branch
            return updateTemplateInstance(sourceName, instance, branch, request, templateDefinition);
        }
        // Existing, normal branch
        else if (existingBranch.get().getType() == BranchType.CLASSIC) {
            throw new BranchClassicCannotBeTemplateInstanceException(branchName);
        }
        // Existing, template branch
        else if (existingBranch.get().getType() == BranchType.TEMPLATE_DEFINITION) {
            throw new BranchTemplateDefinitionCannotBeTemplateInstanceException(branchName);
        } else {
            // Gets the template instance
            Optional<TemplateInstance> templateInstanceOptional = branchTemplateRepository.getTemplateInstance(existingBranch.get().getId());
            // This must be a template instance then...
            Validate.isTrue(templateInstanceOptional.isPresent(), "A template instance branch must have a template instance object attached to it");
            // Gets the linked definition
            ID linkedTemplateId = templateInstanceOptional.get().getTemplateDefinitionId();
            // If another definition, error
            if (!Objects.equals(linkedTemplateId, branchId)) {
                throw new BranchTemplateInstanceCannotUpdateBasedOnOtherDefinitionException(branchName);
            }
            // If same definition, updates the branch
            else {
                return updateTemplateInstance(sourceName, existingBranch.get(), branch, request, templateDefinition);
            }
        }
    }

    @Override
    public Optional<TemplateInstance> getTemplateInstance(ID branchId) {
        return branchTemplateRepository.getTemplateInstance(branchId);
    }

    protected Branch createBranchForTemplateInstance(Branch templateBranch, String branchName) {
        return structureService.newBranch(
                Branch.of(
                        templateBranch.getProject(),
                        NameDescription.nd(
                                NameDescription.escapeName(branchName),
                                ""
                        )
                )
        );
    }

    protected Branch updateTemplateInstance(String sourceName, Branch instance, Branch template, BranchTemplateInstanceSingleRequest request, TemplateDefinition templateDefinition) {
        // Manual mode
        if (request.isManual()) {
            // Missing parameters
            List<String> templateParameterNames = templateDefinition.getParameters().stream()
                    .map(TemplateParameter::getName)
                    .collect(Collectors.toList());
            Set<String> missingParameters = new HashSet<>(templateParameterNames);
            missingParameters.removeAll(request.getParameters().keySet());
            if (missingParameters.size() > 0) {
                throw new BranchTemplateInstanceMissingParametersException(template.getName(), missingParameters);
            }
            // Unknown parameters
            Set<String> unknownParameters = new HashSet<>(request.getParameters().keySet());
            unknownParameters.removeAll(templateParameterNames);
            if (unknownParameters.size() > 0) {
                throw new BranchTemplateInstanceUnknownParametersException(template.getName(), unknownParameters);
            }
            // Adds `branchName` as a parameter
            Map<String, String> engineParams = new HashMap<>(request.getParameters());
            engineParams.put("branchName", sourceName);
            // Replacement function
            Function<String, String> replacementFn = value -> expressionEngine.render(value, engineParams);
            // Template instance execution context
            TemplateInstanceExecution templateInstanceExecution = new TemplateInstanceExecution(
                    replacementFn,
                    request.getParameters()
            );
            // OK
            return updateTemplateInstance(instance, template, templateInstanceExecution);
        }
        // Automatic mode
        else {
            return updateTemplateInstance(sourceName, instance, template, templateDefinition);
        }
    }

    protected Branch updateTemplateInstance(String sourceName, Branch instance, Branch template, TemplateDefinition templateDefinition) {
        return updateTemplateInstance(
                instance,
                template,
                templateDefinition.templateInstanceExecution(sourceName, expressionEngine)
        );

    }

    protected Branch updateTemplateInstance(Branch instance, Branch template, TemplateInstanceExecution templateInstanceExecution) {
        // Description of the branch
        String description = templateInstanceExecution.replace(template.getDescription());
        instance = instance.withDescription(description);
        structureService.saveBranch(instance);
        // Copy replacement function
        copyService.copy(
                instance, // Target
                template, // Source
                templateInstanceExecution.getReplacementFn(),
                SyncPolicy.SYNC
        );
        // Template instance
        TemplateInstance templateInstance = new TemplateInstance(
                template.getId(),
                templateInstanceExecution.asTemplateParameterValues()
        );
        // Branch as a branch template instance
        branchTemplateRepository.setTemplateInstance(instance.getId(), templateInstance);
        // OK - reloads to gets the correct type
        return structureService.getBranch(instance.getId());
    }

    @Override
    public Collection<Job> getJobs() {
        // Gets all template definitions
        return branchTemplateRepository.getTemplateDefinitions().stream()
                // ... filters on interval
                .filter(btd -> (btd.getTemplateDefinition().getInterval() > 0))
                        // ... and creates a sync. job
                .map(this::createTemplateDefinitionSyncJob)
                        // ... ok
                .collect(Collectors.toList());
    }

    protected Job createTemplateDefinitionSyncJob(BranchTemplateDefinition btd) {
        // Loading the branch
        Branch branch = structureService.getBranch(btd.getBranchId());
        // Creating the job
        return new BranchJob(branch) {
            @Override
            public String getCategory() {
                return "BranchTemplateSync";
            }

            @Override
            public String getId() {
                return btd.getBranchId().toString();
            }

            @Override
            public String getDescription() {
                return format(
                        "Template sync. from %s/%s",
                        branch.getProject().getName(),
                        branch.getName()
                );
            }

            @Override
            public int getInterval() {
                return btd.getTemplateDefinition().getInterval();
            }

            @Override
            public JobTask createTask() {
                return new RunnableJobTask(info ->
                        syncTemplateDefinition(branch.getId(), info)
                );
            }
        };
    }

    @Override
    public BranchTemplateSyncResults sync(ID branchId) {
        return syncTemplateDefinition(branchId, logger::info);
    }

    protected BranchTemplateSyncResults syncTemplateDefinition(ID branchId, JobInfoListener info) {
        // Loads the template definition
        info.post(format("Loading template definition for %s", branchId));
        TemplateDefinition templateDefinition = getTemplateDefinition(branchId)
                .orElseThrow(() -> new BranchNotTemplateDefinitionException(branchId));
        // Gets the source of the branch names to synchronise with
        TemplateSynchronisationSource<?> templateSynchronisationSource =
                templateSynchronisationService.getSynchronisationSource(
                        templateDefinition.getSynchronisationSourceConfig().getId()
                );
        // Using the source
        return syncTemplateDefinition(branchId, templateDefinition, templateSynchronisationSource, info);
    }

    protected <T> BranchTemplateSyncResults syncTemplateDefinition(
            ID branchId,
            TemplateDefinition templateDefinition,
            TemplateSynchronisationSource<T> templateSynchronisationSource,
            JobInfoListener info) {
        // Parsing of the configuration data
        T config = templateSynchronisationSource.parseConfig(templateDefinition.getSynchronisationSourceConfig().getData());
        // Loads the branch
        Branch templateBranch = structureService.getBranch(branchId);
        // Logging
        info.post(format("Getting template sync. sources from %s", templateSynchronisationSource.getName()));
        // Getting the list of names
        List<String> sourceNames = templateSynchronisationSource.getBranchNames(templateBranch, config);
        // Sync on those names
        BranchTemplateSyncResults results = syncTemplateDefinition(templateBranch, templateDefinition, sourceNames, info);
        // Reporting
        syncReport(templateBranch, results);
        // OK
        return results;
    }

    private void syncReport(Branch templateBranch, BranchTemplateSyncResults results) {
        for (BranchTemplateSyncResult result : results.getBranches()) {
            syncReport(templateBranch, result);
        }
    }

    private void syncReport(Branch templateBranch, BranchTemplateSyncResult result) {
        eventPostService.post(result.event(structureService, templateBranch));
    }

    protected BranchTemplateSyncResults syncTemplateDefinition(
            Branch templateBranch,
            TemplateDefinition templateDefinition,
            List<String> sourceNames,
            JobInfoListener info) {
        BranchTemplateSyncResults results = new BranchTemplateSyncResults();
        // Sync for each branch in the source names
        List<String> branchNames = new ArrayList<>();
        for (String sourceName : sourceNames) {
            BranchTemplateSyncResult result = syncTemplateDefinition(templateBranch, templateDefinition, sourceName, info);
            branchNames.add(result.getBranchName());
            results.addResult(result);
        }
        // Management of missing branches
        List<String> allInstanceNames = branchTemplateRepository.getTemplateInstancesForDefinition(templateBranch.getId()).stream()
                .map(id -> structureService.getBranch(id.getBranchId()).getName())
                .collect(Collectors.toList());
        // Missing branches in the sources
        List<String> missingBranches = new ArrayList<>(allInstanceNames);
        missingBranches.removeAll(branchNames);
        // For each missing branch, applies the policy
        for (String missingBranchName : missingBranches) {
            results.addResult(
                    applyMissingPolicy(
                            structureService.findBranchByName(templateBranch.getProject().getName(), missingBranchName).get(),
                            templateDefinition.getAbsencePolicy()
                    )
            );
        }
        // OK
        return results;
    }

    protected BranchTemplateSyncResult applyMissingPolicy(Branch branch, TemplateSynchronisationAbsencePolicy absencePolicy) {
        if (branch.isDisabled()) {
            return BranchTemplateSyncResult.ignored(branch.getName());
        } else {
            switch (absencePolicy) {
                case DELETE:
                    structureService.deleteBranch(branch.getId());
                    return BranchTemplateSyncResult.deleted(branch.getName());
                case DISABLE:
                default:
                    structureService.saveBranch(branch.withDisabled(true));
                    return BranchTemplateSyncResult.disabled(branch.getName());
            }
        }
    }

    protected BranchTemplateSyncResult syncTemplateDefinition(Branch templateBranch, TemplateDefinition templateDefinition, String sourceName, JobInfoListener info) {
        // Logging
        info.post(format("Sync. %s --> %s", templateBranch.getName(), sourceName));
        // Gets the target branch, if it exists
        String branchName = NameDescription.escapeName(sourceName);
        Optional<Branch> targetBranch = structureService.findBranchByName(
                templateBranch.getProject().getName(),
                branchName);
        // If it exists, we need to update it
        if (targetBranch.isPresent()) {
            if (targetBranch.get().getType() == BranchType.CLASSIC) {
                return BranchTemplateSyncResult.existingClassic(branchName, sourceName);
            } else if (targetBranch.get().getType() == BranchType.TEMPLATE_DEFINITION) {
                return BranchTemplateSyncResult.existingDefinition(branchName, sourceName);
            } else {
                Optional<TemplateInstance> existingTemplateInstance = branchTemplateRepository.getTemplateInstance(targetBranch.get().getId());
                if (existingTemplateInstance.isPresent() && !existingTemplateInstance.get().getTemplateDefinitionId().equals(templateBranch.getId())) {
                    return BranchTemplateSyncResult.existingInstanceFromOther(
                            branchName,
                            sourceName,
                            structureService.getBranch(existingTemplateInstance.get().getTemplateDefinitionId()).getName()
                    );
                } else {
                    info.post(format("%s exists - updating", branchName));
                    updateTemplateInstance(
                            sourceName,
                            targetBranch.get(),
                            templateBranch,
                            templateDefinition
                    );
                    return BranchTemplateSyncResult.updated(
                            branchName,
                            sourceName
                    );
                }
            }
        }
        // If it does not exist, creates it and updates it
        else {
            info.post(format("%s does not exists - creating and updating", branchName));
            Branch instance = createBranchForTemplateInstance(templateBranch, sourceName);
            updateTemplateInstance(
                    sourceName,
                    instance,
                    templateBranch,
                    templateDefinition
            );
            return BranchTemplateSyncResult.created(
                    branchName,
                    sourceName
            );
        }
    }
}
