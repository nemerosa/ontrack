package net.nemerosa.ontrack.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.buildfilter.*
import net.nemerosa.ontrack.model.exceptions.BuildFilterNotFoundException
import net.nemerosa.ontrack.model.exceptions.BuildFilterNotLoggedException
import net.nemerosa.ontrack.model.security.BranchFilterMgt
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.repository.BuildFilterRepository
import net.nemerosa.ontrack.repository.TBuildFilter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
@Transactional
class BuildFilterServiceImpl(
        buildFilterProviders: Collection<BuildFilterProvider<*>>,
        private val buildFilterRepository: BuildFilterRepository,
        private val structureService: StructureService,
        private val securityService: SecurityService
) : BuildFilterService {

    private val buildFilterProviders: Map<String, BuildFilterProvider<*>> = buildFilterProviders
            .associateBy { it.type }

    override fun defaultFilterProviderData(): BuildFilterProviderData<*> {
        return standardFilterProviderData(10).build()
    }

    override fun lastPromotedBuildsFilterData(): BuildFilterProviderData<*> {
        return getBuildFilterProviderByType<Any>(PromotionLevelBuildFilterProvider::class.java.name)
                ?.withData(null)
                ?: throw BuildFilterProviderNotFoundException(PromotionLevelBuildFilterProvider::class.java.name)
    }

    inner class DefaultStandardFilterProviderDataBuilder(count: Int) : StandardFilterProviderDataBuilder {

        private var data: StandardBuildFilterData = StandardBuildFilterData.of(count)

        override fun build(): BuildFilterProviderData<*> {
            val provider = getBuildFilterProviderByType<Any>(StandardBuildFilterProvider::class.java.name)
                    ?: throw BuildFilterProviderNotFoundException(StandardBuildFilterProvider::class.java.name)
            @Suppress("UNCHECKED_CAST")
            return (provider as BuildFilterProvider<StandardBuildFilterData>).withData(data)
        }

        override fun withSincePromotionLevel(sincePromotionLevel: String): StandardFilterProviderDataBuilder {
            data = data.withSincePromotionLevel(sincePromotionLevel)
            return this
        }

        override fun withWithPromotionLevel(withPromotionLevel: String): StandardFilterProviderDataBuilder {
            data = data.withWithPromotionLevel(withPromotionLevel)
            return this
        }

        override fun withAfterDate(afterDate: LocalDate): StandardFilterProviderDataBuilder {
            data = data.withAfterDate(afterDate)
            return this
        }

        override fun withBeforeDate(beforeDate: LocalDate): StandardFilterProviderDataBuilder {
            data = data.withBeforeDate(beforeDate)
            return this
        }

        override fun withSinceValidationStamp(sinceValidationStamp: String): StandardFilterProviderDataBuilder {
            data = data.withSinceValidationStamp(sinceValidationStamp)
            return this
        }

        override fun withSinceValidationStampStatus(sinceValidationStampStatus: String): StandardFilterProviderDataBuilder {
            data = data.withSinceValidationStampStatus(sinceValidationStampStatus)
            return this
        }

        override fun withWithValidationStamp(withValidationStamp: String): StandardFilterProviderDataBuilder {
            data = data.withWithValidationStamp(withValidationStamp)
            return this
        }

        override fun withWithValidationStampStatus(withValidationStampStatus: String): StandardFilterProviderDataBuilder {
            data = data.withWithValidationStampStatus(withValidationStampStatus)
            return this
        }

        override fun withWithProperty(withProperty: String): StandardFilterProviderDataBuilder {
            data = data.withWithProperty(withProperty)
            return this
        }

        override fun withWithPropertyValue(withPropertyValue: String): StandardFilterProviderDataBuilder {
            data = data.withWithPropertyValue(withPropertyValue)
            return this
        }

        override fun withSinceProperty(sinceProperty: String): StandardFilterProviderDataBuilder {
            data = data.withSinceProperty(sinceProperty)
            return this
        }

        override fun withSincePropertyValue(sincePropertyValue: String): StandardFilterProviderDataBuilder {
            data = data.withSincePropertyValue(sincePropertyValue)
            return this
        }

        override fun withLinkedFrom(linkedFrom: String): StandardFilterProviderDataBuilder {
            data = data.withLinkedFrom(linkedFrom)
            return this
        }

        override fun withLinkedFromPromotion(linkedFromPromotion: String): StandardFilterProviderDataBuilder {
            data = data.withLinkedFromPromotion(linkedFromPromotion)
            return this
        }

        override fun withLinkedTo(linkedTo: String): StandardFilterProviderDataBuilder {
            data = data.withLinkedTo(linkedTo)
            return this
        }

        override fun withLinkedToPromotion(linkedToPromotion: String): StandardFilterProviderDataBuilder {
            data = data.withLinkedToPromotion(linkedToPromotion)
            return this
        }
    }

    override fun standardFilterProviderData(count: Int): StandardFilterProviderDataBuilder {
        return DefaultStandardFilterProviderDataBuilder(count)
    }

    override fun standardFilterProviderData(node: JsonNode): BuildFilterProviderData<*> {
        return getBuildFilterProviderData<Any>(
                StandardBuildFilterProvider::class.java.name,
                node
        )
    }

    override fun getBuildFilters(branchId: ID): Collection<BuildFilterResource<*>> {
        val branch = structureService.getBranch(branchId)
        // Are we logged?
        val account = securityService.currentAccount
        return if (account != null) {
            // Gets the filters for this account and the branch
            buildFilterRepository.findForBranch(OptionalInt.of(account.id()), branchId.value)
                    .mapNotNull { t -> loadBuildFilterResource<Any>(branch, t) }
        }
        // Not logged, no filter
        else {
            // Gets the filters for the branch
            buildFilterRepository.findForBranch(OptionalInt.empty(), branchId.get())
                    .mapNotNull { t -> loadBuildFilterResource<Any>(branch, t) }
        }
    }

    override fun getBuildFilterForms(branchId: ID): Collection<BuildFilterForm> {
        return buildFilterProviders.values
                .map { provider -> provider.newFilterForm(branchId) }
    }

    override fun <T> getBuildFilterProviderData(filterType: String, parameters: JsonNode): BuildFilterProviderData<T> {
        val buildFilterProvider = getBuildFilterProviderByType<T>(filterType)
        return buildFilterProvider
                ?.let { getBuildFilterProviderData(it, parameters) }
                ?: throw BuildFilterProviderNotFoundException(filterType)
    }

    override fun <T> getBuildFilterProviderData(filterType: String, parameters: T): BuildFilterProviderData<T> {
        val buildFilterProvider = getBuildFilterProviderByType<T>(filterType)
        return buildFilterProvider?.withData(parameters)
                ?: throw BuildFilterProviderNotFoundException(filterType)
    }

    protected fun <T> getBuildFilterProviderData(provider: BuildFilterProvider<T>, parameters: JsonNode): BuildFilterProviderData<T> {
        val data = provider.parse(parameters)
        return if (data.isPresent) {
            BuildFilterProviderData.of(provider, data.get())
        } else {
            throw BuildFilterProviderDataParsingException(provider.javaClass.name)
        }
    }

    @Throws(BuildFilterNotFoundException::class)
    override fun getEditionForm(branchId: ID, name: String): BuildFilterForm {
        return securityService.account
                .flatMap { account -> buildFilterRepository.findByBranchAndName(account.id(), branchId.value, name) }
                .orElse(null)
                ?.let { this.getBuildFilterForm<Any>(it) }
                ?: throw BuildFilterNotLoggedException()
    }

    private fun <T> getBuildFilterForm(t: TBuildFilter): BuildFilterForm? {
        val provider = getBuildFilterProviderByType<T>(t.type)
        return provider
                ?.parse(t.data)
                ?.map { data ->
                    provider.getFilterForm(
                            ID.of(t.branchId),
                            data
                    )
                }
                ?.orElse(null)
    }

    override fun saveFilter(branchId: ID, shared: Boolean, name: String, type: String, parameters: JsonNode): Ack {
        // Checks the account
        if (shared) {
            val account = securityService.currentAccount
            // Gets the branch
            val branch = structureService.getBranch(branchId)
            // Checks access rights
            securityService.checkProjectFunction(branch, BranchFilterMgt::class.java)
            // Deletes any previous filter
            val currentAccountId = account.id()
            buildFilterRepository.findByBranchAndName(currentAccountId, branchId.get(), name).ifPresent {
                buildFilterRepository.delete(currentAccountId, branchId.get(), name, true)
            }
            // No account to be used
            return doSaveFilter(OptionalInt.empty(), branchId, name, type, parameters)
        } else {
            val account = securityService.currentAccount
            return if (account == null) {
                Ack.NOK
            } else {
                // Saves it for this account
                doSaveFilter(OptionalInt.of(account.id()), branchId, name, type, parameters)
            }
        }

    }

    private fun doSaveFilter(accountId: OptionalInt, branchId: ID, name: String, type: String, parameters: JsonNode): Ack {
        // Checks the provider
        val provider = getBuildFilterProviderByType<Any>(type)
        return if (provider == null) {
            Ack.NOK
        }
        // Excludes predefined filters
        else if (provider.isPredefined) {
            Ack.NOK
        }
        // Checks the data
        else if (!provider.parse(parameters).isPresent) {
            Ack.NOK
        } else {
            // Saving
            buildFilterRepository.save(accountId, branchId.value, name, type, parameters)
        }
    }

    override fun deleteFilter(branchId: ID, name: String): Ack {
        // Gets the branch
        val branch = structureService.getBranch(branchId)
        // If user is allowed to manage shared filters, this filter might have to be deleted from the shared filters
        // as well
        val sharedFilter = securityService.isProjectFunctionGranted(branch, BranchFilterMgt::class.java)
        // Deleting the filter
        return buildFilterRepository.delete(securityService.currentAccount.id(), branchId.get(), name, sharedFilter)
    }

    override fun copyToBranch(sourceBranchId: ID, targetBranchId: ID) {
        // Gets all the filters for the source branch
        buildFilterRepository.findForBranch(sourceBranchId.value).forEach { filter ->
            buildFilterRepository.save(
                    filter.accountId,
                    targetBranchId.get(),
                    filter.name,
                    filter.type,
                    filter.data
            )
        }
    }

    private fun <T> getBuildFilterProviderByType(type: String): BuildFilterProvider<T>? {
        @Suppress("UNCHECKED_CAST")
        return buildFilterProviders[type] as BuildFilterProvider<T>
    }

    private fun <T> loadBuildFilterResource(branch: Branch, t: TBuildFilter): BuildFilterResource<T>? {
        return getBuildFilterProviderByType<Any>(t.type)
                ?.let {
                    @Suppress("UNCHECKED_CAST")
                    loadBuildFilterResource(it as BuildFilterProvider<T>, branch, t.isShared, t.name, t.data)
                }
    }

    private fun <T> loadBuildFilterResource(provider: BuildFilterProvider<T>, branch: Branch, shared: Boolean, name: String, data: JsonNode): BuildFilterResource<T>? {
        return provider.parse(data)
                .map { parsedData ->
                    BuildFilterResource(
                            branch,
                            shared,
                            name,
                            provider.type,
                            parsedData
                    )
                }.orElse(null)
    }

}
