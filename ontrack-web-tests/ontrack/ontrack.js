import {createProject, getProjectById, projectList} from "@ontrack/project";
import {getValidationRunById} from "@ontrack/validationRun";
import {admin} from "@ontrack/admin";
import {OntrackConfigurations} from "@ontrack/configurations";
import {getBranchById} from "@ontrack/branch";
import {EnvironmentsExtension} from "@ontrack/extensions/environments/environments";

/**
 * Ontrack service
 */
export class Ontrack {
    constructor(connection) {
        this.connection = connection
    }

    admin = () => admin(this)
    configurations = new OntrackConfigurations(this)

    createProject = async (name) => createProject(this, name)
    getProjectById = async (id) => getProjectById(this, id)
    projectList = async () => projectList(this)

    getBranchById = async (id) => getBranchById(this, id)

    getValidationRunById = async (runId) => getValidationRunById(this, runId)

    // Extensions

    environments = new EnvironmentsExtension(this)

    // Cloning with a specific token
    withToken = (token) => new Ontrack(
        this.connection.withToken(token)
    )
}
