package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.model.exceptions.InputException

class CIConfigPRNotSupportedException : InputException("PRs are not supported for CI configuration.")
