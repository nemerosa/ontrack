package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.labels.LabelForm
import net.nemerosa.ontrack.model.labels.LabelProvider
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Service

@Service
class TestLabelProvider(
        private val ontrackConfigProperties: OntrackConfigProperties
) : LabelProvider {
    override val name: String = "Test"


    override val isEnabled: Boolean
        get() = ontrackConfigProperties.isJobLabelProviderTest

    override fun getLabelsForProject(project: Project): List<LabelForm> =
            if (ontrackConfigProperties.isJobLabelProviderTest) {
                listOf(
                        LabelForm(
                                category = "letter",
                                name = project.name.toUpperCase().first().toString(),
                                description = "Letter ${project.name.toUpperCase().first()}",
                                color = "#0000FF"
                        )
                )
            } else {
                emptyList()
            }
}
