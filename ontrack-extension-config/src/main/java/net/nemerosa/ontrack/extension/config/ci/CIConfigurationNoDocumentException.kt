package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.model.exceptions.InputException

class CIConfigurationNoDocumentException :
    InputException("The CI configuration contains no YAML document .")
