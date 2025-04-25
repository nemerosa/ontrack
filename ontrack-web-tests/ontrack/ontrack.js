import {backend, credentials, ui} from "@ontrack/connection";
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

    createProject = async (name) => createProject(this, name)
    getProjectById = async (id) => getProjectById(this, id)

    getBranchById = async (id) => getBranchById(this, id)
}

/**
 * Ontrack root
 *
 * @deprecated Use the `ontrack` fixture
 */
export const ontrack = (customCredentials) => {
    const connection = {
        ui: ui(),
        backend: backend(),
        credentials: customCredentials ?? credentials(),
    }

    const self = {
        connection,
    }

    self.projectList = async () => projectList(self)

    self.getValidationRunById = async (runId) => getValidationRunById(self, runId)

    self.admin = () => admin(self)
    self.configurations = new OntrackConfigurations(self)

    // Extensions
    self.environments = new EnvironmentsExtension(self)

    return self
}
