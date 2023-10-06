package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.exceptions.InputException

class BranchSourceMissingConfigurationException(id: String) :
    InputException("Branch source $id is missing its configuration")
