export const callDynamicFunction = async (moduleName, ...args) => {
    try {
        const loadedModule = await import(`../${moduleName}`);
        if (loadedModule.default) {
            // Check if there is a default export
            return loadedModule.default(...args)
        } else {
            console.error("No default export found in the module.")
            return null
        }
    } catch (error) {
        if (error.code === 'MODULE_NOT_FOUND') {
            return null
        } else {
            throw (`Error loading the module: ${error}`)
        }
    }
}
