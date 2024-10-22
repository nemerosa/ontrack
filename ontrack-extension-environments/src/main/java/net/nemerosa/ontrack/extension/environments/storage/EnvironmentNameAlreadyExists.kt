package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.model.exceptions.InputException

class EnvironmentNameAlreadyExists(name: String) : InputException("Environment $name already exists.")
