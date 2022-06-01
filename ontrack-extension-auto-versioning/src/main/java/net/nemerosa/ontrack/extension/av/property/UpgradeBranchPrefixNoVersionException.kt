package net.nemerosa.ontrack.extension.av.property

import net.nemerosa.ontrack.model.exceptions.InputException

/**
 * This exception is thrown when a [AutoVersioningConfig.upgradeBranchPattern] property
 * does not contain the `<version>` token.
 */
class UpgradeBranchPrefixNoVersionException(value: String) : InputException(
    "Upgrade branch prefix must contain the `<version>` token: $value"
)
