package net.nemerosa.ontrack.extension.indicators.model.scale

data class ScaleValue<S : Scale<S>>(
        val factory: ScaleFactory<S>,
        val value: S
)
