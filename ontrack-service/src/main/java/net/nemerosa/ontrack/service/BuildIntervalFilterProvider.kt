package net.nemerosa.ontrack.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.repository.CoreBuildFilterRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
@Transactional
class BuildIntervalFilterProvider(
        private val filterRepository: CoreBuildFilterRepository
) : AbstractBuildFilterProvider<BuildIntervalFilterData>() {

    override fun getType(): String = BuildIntervalFilterProvider::class.java.name

    override fun fill(form: Form, data: BuildIntervalFilterData): Form {
        return form
                .fill("from", data.from)
                .fill("to", data.to)
    }

    override fun blankForm(branchId: ID): Form = Form.create()
            .with(
                    Text.of("from")
                            .label("From build")
                            .help("First build")
            )
            .with(
                    Text.of("to")
                            .label("To build")
                            .optional()
                            .help("Last build")
            )

    override fun getName(): String = "Build interval"

    override fun isPredefined(): Boolean = false

    override fun filterBranchBuilds(branch: Branch, data: BuildIntervalFilterData): List<Build> {
        return filterRepository.between(branch, data.from, data.to)
    }

    override fun parse(data: JsonNode): Optional<BuildIntervalFilterData> {
        return Optional.of(
                BuildIntervalFilterData(
                        JsonUtils.get(data, "from", true, null),
                        JsonUtils.get(data, "to", true, null)
                )
        )
    }
}
