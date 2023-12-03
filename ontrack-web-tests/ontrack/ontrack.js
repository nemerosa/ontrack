import {backend, credentials, ui} from "@ontrack/connection";
import {createProject} from "@ontrack/project";

/**
 * Ontrack root
 */
export const ontrack = () => {
    const connection = {
        ui: ui(),
        backend: backend(),
        credentials: credentials(),
    }

    const self = {
        connection,
    }

    self.createProject = async (name) => createProject(self, name)

    return self
}
