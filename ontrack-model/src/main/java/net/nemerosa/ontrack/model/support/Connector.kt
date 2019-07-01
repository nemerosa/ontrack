package net.nemerosa.ontrack.model.support

data class Connector(
        val type: String,
        val name: String
) : Comparable<Connector> {

    override fun compareTo(other: Connector): Int =
            compareValuesBy(this, other, Connector::type, Connector::name)

}
