package net.nemerosa.ontrack.service.ordering

import net.nemerosa.ontrack.model.ordering.BranchOrderingService
import net.nemerosa.ontrack.model.ordering.VersionOrName
import net.nemerosa.ontrack.model.ordering.VersionUtils
import net.nemerosa.ontrack.model.ordering.VersionUtils.semVerSuffixRegex
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import net.nemerosa.ontrack.model.structure.BranchNamePolicy
import org.springframework.stereotype.Service

@Service
class BranchOrderingServiceImpl(
    private val branchDisplayNameService: BranchDisplayNameService,
) : BranchOrderingService {

    override fun getRegexVersionComparator(regex: Regex): Comparator<Branch> {
        return compareByDescending { branch -> getVersion(branch, regex) }
    }

    internal fun getVersion(branch: Branch, regex: Regex): VersionOrName {
        // Path to use for the branch
        val path: String = branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_OR_NAME)
        // Path first, then name, then version = name
        return VersionUtils.getVersion(regex, path) ?: VersionOrName(path)
    }

    override fun getSemVerBranchOrdering(branchNamePolicy: BranchNamePolicy): Comparator<Branch> {
        return compareByDescending { branch ->
            val name = branchDisplayNameService.getBranchDisplayName(branch, branchNamePolicy)
            VersionUtils.getVersion(semVerSuffixRegex, name)
        }
    }
}