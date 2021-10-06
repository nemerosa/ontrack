package net.nemerosa.ontrack.extension.issues.combined

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.extension.issues.model.SelectableIssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.form.MultiSelection
import net.nemerosa.ontrack.model.support.Configuration
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor

class CombinedIssueServiceConfiguration(
    override val name: String,
    val issueServiceConfigurationIdentifiers: List<String>
) : Configuration<CombinedIssueServiceConfiguration>, IssueServiceConfiguration {

    override val serviceId: String = CombinedIssueServiceExtension.SERVICE

    @get:JsonIgnore
    override val descriptor: ConfigurationDescriptor
        get() = ConfigurationDescriptor(name, name)

    override fun obfuscate(): CombinedIssueServiceConfiguration = this

    override fun injectCredentials(oldConfig: CombinedIssueServiceConfiguration): CombinedIssueServiceConfiguration = this

    override fun encrypt(crypting: (plain: String?) -> String?): CombinedIssueServiceConfiguration = this

    override fun decrypt(decrypting: (encrypted: String?) -> String?): CombinedIssueServiceConfiguration = this

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
                                    issueServiceConfigurationIdentifiers.contains(
                                        issueServiceConfigurationRepresentation.id
                                    )
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
