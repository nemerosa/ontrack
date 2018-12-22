package net.nemerosa.ontrack.git.model.plot

abstract class AbstractGItem : GItem {

    override val type: String
        get() = javaClass.simpleName.substring(1).toLowerCase()

}
