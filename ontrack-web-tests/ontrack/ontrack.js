import {backend, credentials, ui} from "@ontrack/connection";
import {createProject, getProjectById, projectList} from "@ontrack/project";
import {getValidationRunById} from "@ontrack/validationRun";
import {admin} from "@ontrack/admin";
import {OntrackConfigurations} from "@ontrack/configurations";
import {getBranchById} from "@ontrack/branch";
import {EnvironmentsExtension} from "@ontrack/extensions/environments/environments";

/**
 * Ontrack root
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

    self.createProject = async (name) => createProject(self, name)
    self.projectList = async () => projectList(self)

    self.getProjectById = async (id) => getProjectById(self, id)
    self.getBranchById = async (id) => getBranchById(self, id)

    self.getValidationRunById = async (runId) => getValidationRunById(self, runId)

    self.admin = () => admin(self)
    self.configurations = new OntrackConfigurations(self)

    // Extensions
    self.environments = new EnvironmentsExtension(self)

    return self
}
