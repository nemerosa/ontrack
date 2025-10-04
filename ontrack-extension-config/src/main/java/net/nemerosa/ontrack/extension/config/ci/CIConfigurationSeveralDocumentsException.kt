package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.model.exceptions.InputException

class CIConfigurationSeveralDocumentsException :
    InputException("The CI configuration contains more than one YAML document .")
