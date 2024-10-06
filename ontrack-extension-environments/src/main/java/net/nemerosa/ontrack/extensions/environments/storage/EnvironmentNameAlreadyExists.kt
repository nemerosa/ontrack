package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.model.exceptions.InputException

class EnvironmentNameAlreadyExists(name: String) : InputException("Environment $name already exists.")
