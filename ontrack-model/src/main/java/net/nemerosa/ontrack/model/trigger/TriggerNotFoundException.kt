package net.nemerosa.ontrack.model.trigger

import net.nemerosa.ontrack.common.BaseException

class TriggerNotFoundException(id: String) : BaseException("Trigger not found: $id")
