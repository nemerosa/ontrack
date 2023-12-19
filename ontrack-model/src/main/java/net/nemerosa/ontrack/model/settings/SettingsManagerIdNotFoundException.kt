package net.nemerosa.ontrack.model.settings

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class SettingsManagerIdNotFoundException(id: String) :
    NotFoundException("Settings with ID $id cannot be found")
