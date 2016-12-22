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
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.repository.BuildFilterRepository;
import net.nemerosa.ontrack.repository.TBuildFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildFilterServiceImpl implements BuildFilterService {

    private final Map<String, BuildFilterProvider<?>> buildFilterProviders;
    private final BuildFilterRepository buildFilterRepository;
    private final StructureService structureService;
    private final SecurityService securityService;

    @Autowired
    public BuildFilterServiceImpl(
            Collection<BuildFilterProvider<?>> buildFilterProviders,
            BuildFilterRepository buildFilterRepository,
            StructureService structureService,
            SecurityService securityService) {
        this.buildFilterProviders = buildFilterProviders.stream()
                .collect(Collectors.toMap(
                        BuildFilterProvider::getType,
                        Function.identity()
                ));
        this.buildFilterRepository = buildFilterRepository;
        this.structureService = structureService;
        this.securityService = securityService;
    }

    @Override
    public BuildFilterProviderData<?> defaultFilterProviderData() {
        return standardFilterProviderData(1).build();
    }

    @Override
    public BuildFilterProviderData<?> lastPromotedBuildsFilterData() {
        BuildFilterProvider<?> provider =
                getBuildFilterProviderByType(PromotionLevelBuildFilterProvider.class.getName())
                        .orElseThrow(() -> new BuildFilterProviderNotFoundException(
                                PromotionLevelBuildFilterProvider.class.getName())
                        );
        return provider.withData(null);
    }

    public class DefaultStandardFilterProviderDataBuilder implements StandardFilterProviderDataBuilder {

        private StandardBuildFilterData data;

        public DefaultStandardFilterProviderDataBuilder(int count) {
            data = StandardBuildFilterData.of(count);
        }

        @Override
        public BuildFilterProviderData<?> build() {
            BuildFilterProvider<?> provider =
                    getBuildFilterProviderByType(StandardBuildFilterProvider.class.getName())
                            .orElseThrow(() -> new BuildFilterProviderNotFoundException(
                                    StandardBuildFilterProvider.class.getName())
                            );
            //noinspection unchecked
            return ((BuildFilterProvider<StandardBuildFilterData>) provider).withData(data);
        }

        @Override
        public StandardFilterProviderDataBuilder withSincePromotionLevel(String sincePromotionLevel) {
            data = data.withSincePromotionLevel(sincePromotionLevel);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withWithPromotionLevel(String withPromotionLevel) {
            data = data.withWithPromotionLevel(withPromotionLevel);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withAfterDate(LocalDate afterDate) {
            data = data.withAfterDate(afterDate);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withBeforeDate(LocalDate beforeDate) {
            data = data.withBeforeDate(beforeDate);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withSinceValidationStamp(String sinceValidationStamp) {
            data = data.withSinceValidationStamp(sinceValidationStamp);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withSinceValidationStampStatus(String sinceValidationStampStatus) {
            data = data.withSinceValidationStampStatus(sinceValidationStampStatus);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withWithValidationStamp(String withValidationStamp) {
            data = data.withWithValidationStamp(withValidationStamp);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withWithValidationStampStatus(String withValidationStampStatus) {
            data = data.withWithValidationStampStatus(withValidationStampStatus);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withWithProperty(String withProperty) {
            data = data.withWithProperty(withProperty);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withWithPropertyValue(String withPropertyValue) {
            data = data.withWithPropertyValue(withPropertyValue);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withSinceProperty(String sinceProperty) {
            data = data.withSinceProperty(sinceProperty);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withSincePropertyValue(String sincePropertyValue) {
            data = data.withSincePropertyValue(sincePropertyValue);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withLinkedFrom(String linkedFrom) {
            data = data.withLinkedFrom(linkedFrom);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withLinkedFromPromotion(String linkedFromPromotion) {
            data = data.withLinkedFromPromotion(linkedFromPromotion);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withLinkedTo(String linkedTo) {
            data = data.withLinkedTo(linkedTo);
            return this;
        }

        @Override
        public StandardFilterProviderDataBuilder withLinkedToPromotion(String linkedToPromotion) {
            data = data.withLinkedToPromotion(linkedToPromotion);
            return this;
        }
    }

    @Override
    public StandardFilterProviderDataBuilder standardFilterProviderData(int count) {
        return new DefaultStandardFilterProviderDataBuilder(count);
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
        return buildFilterProviders.values().stream()
                .map(provider -> provider.newFilterForm(branchId))
                .collect(Collectors.toList());
    }

    @Override
    public <T> BuildFilterProviderData<T> getBuildFilterProviderData(String filterType, JsonNode parameters) {
        Optional<? extends BuildFilterProvider<T>> o = getBuildFilterProviderByType(filterType);
        if (o.isPresent()) {
            //noinspection unchecked
            return getBuildFilterProviderData(o.get(), parameters);
        } else {
            throw new BuildFilterProviderNotFoundException(filterType);
        }
    }

    @Override
    public <T> BuildFilterProviderData<T> getBuildFilterProviderData(String filterType, T parameters) {
        Optional<? extends BuildFilterProvider<T>> o = getBuildFilterProviderByType(filterType);
        if (o.isPresent()) {
            //noinspection unchecked
            return o.get().withData(parameters);
        } else {
            throw new BuildFilterProviderNotFoundException(filterType);
        }
    }

    protected <T> BuildFilterProviderData<T> getBuildFilterProviderData(BuildFilterProvider<T> provider, JsonNode parameters) {
        Optional<T> data = provider.parse(parameters);
        if (data.isPresent()) {
            return BuildFilterProviderData.of(provider, data.get());
        } else {
            throw new BuildFilterProviderDataParsingException(provider.getClass().getName());
        }
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

    @SuppressWarnings("unchecked")
    private <T> Optional<? extends BuildFilterProvider<T>> getBuildFilterProviderByType(String type) {
        return Optional.ofNullable((BuildFilterProvider<T>) buildFilterProviders.get(type));
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
                        provider.getType(),
                        parsedData
                )
        );
    }

}
