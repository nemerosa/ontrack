import {backend, credentials, ui} from "@ontrack/connection";
import {createProject, projectList} from "@ontrack/project";
import {getValidationRunById} from "@ontrack/validationRun";
import {admin} from "@ontrack/admin";
import {configurations, OntrackConfigurations} from "@ontrack/configurations";

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

    self.getValidationRunById = async (runId) => getValidationRunById(self, runId)

    self.admin = () => admin(self)
    self.configurations = new OntrackConfigurations(self)

    return self
}
