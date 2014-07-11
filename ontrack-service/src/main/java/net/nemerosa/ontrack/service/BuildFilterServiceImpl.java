package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.buildfilter.*;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.repository.BuildFilterRepository;
import net.nemerosa.ontrack.repository.TBuildFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildFilterServiceImpl implements BuildFilterService {

    private final Collection<BuildFilterProvider> buildFilterProviders;
    private final BuildFilterRepository buildFilterRepository;
    private final SecurityService securityService;

    @Autowired
    public BuildFilterServiceImpl(
            Collection<BuildFilterProvider> buildFilterProviders,
            BuildFilterRepository buildFilterRepository,
            SecurityService securityService) {
        this.buildFilterProviders = buildFilterProviders;
        this.buildFilterRepository = buildFilterRepository;
        this.securityService = securityService;
    }

    @Override
    public BuildFilter defaultFilter() {
        return DefaultBuildFilter.INSTANCE;
    }

    @Override
    public BuildFilters getBuildFilters(ID branchId) {
        return new BuildFilters(
                getBuildFilterForms(branchId),
                loadExistingFilters(branchId)
        );
    }

    @Override
    public BuildFilter computeFilter(ID branchId, String type, JsonNode jsonData) {
        Optional<BuildFilter> optFilter = getBuildFilterProviderByType(type).map(provider -> getBuildFilter(branchId, provider, jsonData));
        return optFilter.get();
    }

    private <T> BuildFilter getBuildFilter(ID branchId, BuildFilterProvider<T> provider, JsonNode jsonData) {
        return provider.parse(jsonData)
                .map(data -> provider.filter(branchId, data))
                .orElse(defaultFilter());
    }

    private Optional<BuildFilterProvider> getBuildFilterProviderByType(String type) {
        return buildFilterProviders.stream()
                .filter(provider -> type.equals(provider.getClass().getName()))
                .findFirst();
    }

    private Collection<BuildFilterResource> loadExistingFilters(ID branchId) {
        // Are we logged?
        Account account = securityService.getCurrentAccount();
        if (account != null) {
            // Gets the filters for this account and the branch
            return buildFilterRepository.findForBranch(account.id(), branchId.getValue()).stream()
                    .map(this::loadBuildFilterResource)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        // Not logged, no filter
        else {
            return Collections.emptyList();
        }
    }

    private <T> Optional<BuildFilterResource<T>> loadBuildFilterResource(TBuildFilter t) {
        return getBuildFilterProviderByType(t.getType())
                .flatMap(provider -> loadBuildFilterResource(provider, t.getBranchId(), t.getName(), t.getData()));
    }

    private <T> Optional<BuildFilterResource<T>> loadBuildFilterResource(BuildFilterProvider<T> provider, int branchId, String name, JsonNode data) {
        return provider.parse(data).map(parsedData ->
                        new BuildFilterResource<>(
                                name,
                                provider.getFilterForm(ID.of(branchId), parsedData),
                                parsedData
                        )
        );
    }

    private void storeFilter(ID branchId, BuildFilterProvider buildFilterProvider, String name, Map<String, String> parameters) {
        // TODO Stores the build filter
    }

    private Collection<BuildFilterForm> getBuildFilterForms(ID branchId) {
        return buildFilterProviders.stream()
                .map(provider -> provider.newFilterForm(branchId))
                .collect(Collectors.toList());
    }

}
