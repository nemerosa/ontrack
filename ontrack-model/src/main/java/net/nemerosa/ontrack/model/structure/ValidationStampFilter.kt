package net.nemerosa.ontrack.model.structure

open class ValidationStampFilter(
        private val id: ID = ID.NONE,
        val name: String,
        val vsNames: List<String>,
        val project: Project? = null,
        val branch: Branch? = null
) : Entity {

    override fun getId(): ID = id

    fun withName(name: String): ValidationStampFilter {
        return ValidationStampFilter(
                id,
                name,
                vsNames,
                project,
                branch
        )
    }

    fun withVsNames(vsNames: List<String>): ValidationStampFilter {
        return ValidationStampFilter(
                id,
                name,
                vsNames,
                project,
                branch
        )
    }

    fun withId(id: ID): ValidationStampFilter {
        return ValidationStampFilter(
                id,
                name,
                vsNames,
                project,
                branch
        )
    }

    fun withProject(project: Project?): ValidationStampFilter {
        return ValidationStampFilter(
                id,
                name,
                vsNames,
                project,
                branch
        )
    }

    fun withBranch(branch: Branch?): ValidationStampFilter {
        return ValidationStampFilter(
                id,
                name,
                vsNames,
                project,
                branch
        )
    }

    val scope: ValidationStampFilterScope
        get() = when {
            branch != null -> ValidationStampFilterScope.BRANCH
            project != null -> ValidationStampFilterScope.PROJECT
            else -> ValidationStampFilterScope.GLOBAL
        }

}
