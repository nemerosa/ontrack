package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.*;
import net.nemerosa.ontrack.model.exceptions.BuildFilterNotFoundException;
import net.nemerosa.ontrack.model.exceptions.BuildFilterNotLoggedException;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.BranchFilterMgt;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.repository.BuildFilterRepository;
import net.nemerosa.ontrack.repository.TBuildFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildFilterServiceImpl implements BuildFilterService {

    private final Collection<BuildFilterProvider<?>> buildFilterProviders;
    private final BuildFilterRepository buildFilterRepository;
    private final StructureService structureService;
    private final SecurityService securityService;
    private final PropertyService propertyService;

    @Autowired
    public BuildFilterServiceImpl(
            Collection<BuildFilterProvider<?>> buildFilterProviders,
            BuildFilterRepository buildFilterRepository,
            StructureService structureService, SecurityService securityService, PropertyService propertyService) {
        this.buildFilterProviders = buildFilterProviders;
        this.buildFilterRepository = buildFilterRepository;
        this.structureService = structureService;
        this.securityService = securityService;
        this.propertyService = propertyService;
    }

    @Override
    public BuildFilter defaultFilter() {
        return DefaultBuildFilter.INSTANCE;
    }

    @Override
    public StandardFilterBuilder standardFilter(int count) {
        return new StandardFilterBuilder() {

            private final StandardBuildFilterData data = StandardBuildFilterData.of(count);

            @Override
            public BuildFilter build() {
                return new StandardBuildFilter(data, propertyService);
            }

            @Override
            public StandardBuildFilterData withSincePromotionLevel(String sincePromotionLevel) {
                return data.withSincePromotionLevel(sincePromotionLevel);
            }

            @Override
            public StandardBuildFilterData withWithPromotionLevel(String withPromotionLevel) {
                return data.withWithPromotionLevel(withPromotionLevel);
            }

            @Override
            public StandardBuildFilterData withAfterDate(LocalDate afterDate) {
                return data.withAfterDate(afterDate);
            }

            @Override
            public StandardBuildFilterData withBeforeDate(LocalDate beforeDate) {
                return data.withBeforeDate(beforeDate);
            }

            @Override
            public StandardBuildFilterData withSinceValidationStamp(String sinceValidationStamp) {
                return data.withSinceValidationStamp(sinceValidationStamp);
            }

            @Override
            public StandardBuildFilterData withSinceValidationStampStatus(String sinceValidationStampStatus) {
                return data.withSinceValidationStampStatus(sinceValidationStampStatus);
            }

            @Override
            public StandardBuildFilterData withWithValidationStamp(String withValidationStamp) {
                return data.withWithValidationStamp(withValidationStamp);
            }

            @Override
            public StandardBuildFilterData withWithValidationStampStatus(String withValidationStampStatus) {
                return data.withWithValidationStampStatus(withValidationStampStatus);
            }

            @Override
            public StandardBuildFilterData withWithProperty(String withProperty) {
                return data.withWithProperty(withProperty);
            }

            @Override
            public StandardBuildFilterData withWithPropertyValue(String withPropertyValue) {
                return data.withWithPropertyValue(withPropertyValue);
            }

            @Override
            public StandardBuildFilterData withSinceProperty(String sinceProperty) {
                return data.withSinceProperty(sinceProperty);
            }

            @Override
            public StandardBuildFilterData withSincePropertyValue(String sincePropertyValue) {
                return data.withSincePropertyValue(sincePropertyValue);
            }
        };
    }

    @Override
    public Collection<BuildFilterResource<?>> getBuildFilters(ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        // Are we logged?
        Account account = securityService.getCurrentAccount();
        if (account != null) {
            // Gets the filters for this account and the branch
            return buildFilterRepository.findForBranch(OptionalInt.of(account.id()), branchId.getValue()).stream()
                    .map(t -> loadBuildFilterResource(branch, t))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        // Not logged, no filter
        else {
            // Gets the filters for the branch
            return buildFilterRepository.findForBranch(OptionalInt.empty(), branchId.get()).stream()
                    .map(t -> loadBuildFilterResource(branch, t))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Collection<BuildFilterForm> getBuildFilterForms(ID branchId) {
        return buildFilterProviders.stream()
                .map(provider -> provider.newFilterForm(branchId))
                .collect(Collectors.toList());
    }

    @Override
    public BuildFilter computeFilter(ID branchId, String type, JsonNode jsonData) {
        Optional<BuildFilter> optFilter = getBuildFilterProviderByType(type).map(provider -> getBuildFilter(branchId, provider, jsonData));
        return optFilter.get();
    }

    @Override
    public BuildFilterForm getEditionForm(ID branchId, String name) throws BuildFilterNotFoundException {
        return securityService.getAccount()
                .flatMap(account -> buildFilterRepository.findByBranchAndName(account.id(), branchId.getValue(), name))
                .flatMap(this::getBuildFilterForm)
                .orElseThrow(BuildFilterNotLoggedException::new);
    }

    private <T> Optional<BuildFilterForm> getBuildFilterForm(TBuildFilter t) {
        Optional<? extends BuildFilterProvider<T>> optProvider = getBuildFilterProviderByType(t.getType());
        if (optProvider.isPresent()) {
            return optProvider.get().parse(t.getData()).map(data -> optProvider.get().getFilterForm(
                    ID.of(t.getBranchId()),
                    data
            ));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Ack saveFilter(ID branchId, boolean shared, String name, String type, JsonNode parameters) {
        // Checks the account
        if (shared) {
            Account account = securityService.getCurrentAccount();
            // Gets the branch
            Branch branch = structureService.getBranch(branchId);
            // Checks access rights
            securityService.checkProjectFunction(branch, BranchFilterMgt.class);
            // Deletes any previous filter
            int currentAccountId = account.id();
            buildFilterRepository.findByBranchAndName(currentAccountId, branchId.get(), name).ifPresent(
                    (filter) -> buildFilterRepository.delete(currentAccountId, branchId.get(), name, true)
            );
            // No account to be used
            return doSaveFilter(OptionalInt.empty(), branchId, name, type, parameters);
        } else {
            Account account = securityService.getCurrentAccount();
            if (account == null) {
                return Ack.NOK;
            } else {
                // Saves it for this account
                return doSaveFilter(OptionalInt.of(account.id()), branchId, name, type, parameters);
            }
        }

    }

    private Ack doSaveFilter(OptionalInt accountId, ID branchId, String name, String type, JsonNode parameters) {
        // Checks the provider
        Optional<? extends BuildFilterProvider<Object>> provider = getBuildFilterProviderByType(type);
        if (!provider.isPresent()) {
            return Ack.NOK;
        }
        // Excludes predefined filters
        if (provider.get().isPredefined()) {
            return Ack.NOK;
        }
        // Checks the data
        if (!provider.get().parse(parameters).isPresent()) {
            return Ack.NOK;
        }
        // Saving
        return buildFilterRepository.save(accountId, branchId.getValue(), name, type, parameters);
    }

    @Override
    public Ack deleteFilter(ID branchId, String name) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // If user is allowed to manage shared filters, this filter might have to be deleted from the shared filters
        // as well
        boolean sharedFilter = securityService.isProjectFunctionGranted(branch, BranchFilterMgt.class);
        // Deleting the filter
        return buildFilterRepository.delete(securityService.getCurrentAccount().id(), branchId.get(), name, sharedFilter);
    }

    @Override
    public void copyToBranch(ID sourceBranchId, ID targetBranchId) {
        // Gets all the filters for the source branch
        buildFilterRepository.findForBranch(sourceBranchId.getValue()).forEach(filter ->
                buildFilterRepository.save(
                        filter.getAccountId(),
                        targetBranchId.get(),
                        filter.getName(),
                        filter.getType(),
                        filter.getData()
                )
        );
    }

    private <T> BuildFilter getBuildFilter(ID branchId, BuildFilterProvider<T> provider, JsonNode jsonData) {
        return provider.parse(jsonData)
                .map(data -> provider.filter(branchId, data))
                .orElse(defaultFilter());
    }

    private <T> Optional<? extends BuildFilterProvider<T>> getBuildFilterProviderByType(String type) {
        Optional<? extends BuildFilterProvider<?>> first = buildFilterProviders.stream()
                .filter(provider -> type.equals(provider.getClass().getName()))
                .findFirst();
        //noinspection unchecked
        return (Optional<? extends BuildFilterProvider<T>>) first;
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<BuildFilterResource<T>> loadBuildFilterResource(Branch branch, TBuildFilter t) {
        return getBuildFilterProviderByType(t.getType())
                .flatMap(provider -> loadBuildFilterResource((BuildFilterProvider<T>) provider, branch, t.isShared(), t.getName(), t.getData()));
    }

    private <T> Optional<BuildFilterResource<T>> loadBuildFilterResource(BuildFilterProvider<T> provider, Branch branch, boolean shared, String name, JsonNode data) {
        return provider.parse(data).map(parsedData ->
                new BuildFilterResource<>(
                        branch,
                        shared,
                        name,
                        provider.getClass().getName(),
                        parsedData
                )
        );
    }

}
