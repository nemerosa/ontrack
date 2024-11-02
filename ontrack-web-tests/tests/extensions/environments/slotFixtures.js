export const createSlot = async (ontrack) => {
    const environment = await ontrack.environments.createEnvironment({})
    const project = await ontrack.createProject()
    const slot = await environment.createSlot({project})
    return {
        environment,
        project,
        slot,
    }
}