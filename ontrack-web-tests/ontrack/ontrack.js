import {backend, credentials, ui} from "@ontrack/connection";
import {createProject, projectList} from "@ontrack/project";
import {getValidationRunById} from "@ontrack/validationRun";
import {admin} from "@ontrack/admin";

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

    return self
}
