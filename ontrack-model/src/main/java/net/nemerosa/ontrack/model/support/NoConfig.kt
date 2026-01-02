package net.nemerosa.ontrack.model.support

import com.fasterxml.jackson.annotation.JsonIgnore

data class NoConfig(
    @JsonIgnore
    val ignored: String = ""
) {
    companion object {
        val INSTANCE: NoConfig = NoConfig()
    }
}
