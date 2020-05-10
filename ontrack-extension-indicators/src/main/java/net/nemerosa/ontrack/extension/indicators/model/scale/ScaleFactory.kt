package net.nemerosa.ontrack.extension.indicators.model.scale

interface ScaleFactory<S : Scale<S>> {

    val name: String
    val description: String

    fun toScale(value: Int): S

}