package net.nemerosa.ontrack.extension.issues.combined

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.extension.issues.model.SelectableIssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.MultiSelection
import net.nemerosa.ontrack.model.support.Configuration
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor

import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField

class CombinedIssueServiceConfiguration(
        private val name: String,
        val issueServiceConfigurationIdentifiers: List<String>
) : Configuration<CombinedIssueServiceConfiguration>, IssueServiceConfiguration {

    override fun getName(): String = name

    @JsonIgnore
    override fun getServiceId(): String = CombinedIssueServiceExtension.SERVICE

    @JsonIgnore
    override fun getDescriptor(): ConfigurationDescriptor = ConfigurationDescriptor(name, name)

    override fun obfuscate(): CombinedIssueServiceConfiguration = this

    fun asForm(availableIssueServiceConfigurations: List<IssueServiceConfigurationRepresentation>): Form {
        return Form.create()
                .with(defaultNameField().value(name))
                .with(
                        MultiSelection.of("issueServiceConfigurationIdentifiers")
                                .label("Issue services")
                                .help("List of issue services to combine.")
                                .items(
                                        availableIssueServiceConfigurations
                                                .map { issueServiceConfigurationRepresentation ->
                                                    SelectableIssueServiceConfigurationRepresentation(
                                                            issueServiceConfigurationRepresentation,
                                                            issueServiceConfigurationIdentifiers.contains(issueServiceConfigurationRepresentation.id)
                                                    )
                                                }

                                )
                )
    }

    companion object {
        @JvmStatic
        fun form(availableIssueServiceConfigurations: List<IssueServiceConfigurationRepresentation>): Form {
            return CombinedIssueServiceConfiguration(
                    "",
                    emptyList()
            ).asForm(availableIssueServiceConfigurations)
        }
    }
}
