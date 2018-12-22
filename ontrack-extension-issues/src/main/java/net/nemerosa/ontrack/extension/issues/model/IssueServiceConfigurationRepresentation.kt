package net.nemerosa.ontrack.extension.issues.model

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension

/**
 * This class is used to represent an [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration]
 * to a client. The proposed [id][.id] is composed from the
 * [service id][net.nemerosa.ontrack.extension.issues.IssueServiceExtension.getId] and from the
 * [configuration name][IssueServiceConfiguration.getName], separated by `//`.
 *
 * @see net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationIdentifier
 */
class IssueServiceConfigurationRepresentation(
        val id: String,
        val name: String,
        val serviceId: String
) {

    companion object {

        private const val SELF_ID = "self"

        fun of(issueServiceExtension: IssueServiceExtension, issueServiceConfiguration: IssueServiceConfiguration): IssueServiceConfigurationRepresentation {
            return IssueServiceConfigurationRepresentation(
                    issueServiceConfiguration.toIdentifier().format(),
                    String.format("%s (%s)", issueServiceConfiguration.name, issueServiceExtension.name),
                    issueServiceExtension.id
            )
        }

        /**
         * Checks if a representation ID designates the special `self` issue service.
         *
         * @param id ID to check
         */
        @JvmStatic
        fun isSelf(id: String?): Boolean {
            return SELF_ID == id
        }

        /**
         * Special representation used to designate an issue service which is linked to another configuration.
         * For example, the GitLab issue service linked to the GitLab Git configuration.
         *
         * @param name      Display name
         * @param serviceId [IssueServiceExtension.getId] of the service
         * @see IssueServiceExtension.getId
         */
        fun self(name: String, serviceId: String): IssueServiceConfigurationRepresentation {
            return IssueServiceConfigurationRepresentation(
                    SELF_ID,
                    name,
                    serviceId
            )
        }
    }
}
