package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.annotations.APIDescription

class ValidationStampFilter(
        @APIDescription("ID of the filter")
        override val id: ID = ID.NONE,
        @APIDescription("Name of the filter")
        val name: String,
        @APIDescription("List of validation stamps")
        val vsNames: List<String>,
        @APIDescription("Project level")
        val project: Project? = null,
        @APIDescription("Branch level")
        val branch: Branch? = null
) : Entity {

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

    @APIDescription("Scope of the filter")
    val scope: ValidationStampFilterScope
        get() = when {
            branch != null -> ValidationStampFilterScope.BRANCH
            project != null -> ValidationStampFilterScope.PROJECT
            else -> ValidationStampFilterScope.GLOBAL
        }

}
