import {createContext} from "react";

export const AutoVersioningAuditContext = createContext({
    sourceProject: undefined,
    targetProject: undefined,
    targetBranch: undefined,
})

export default function AutoVersioningAuditContextProvider({sourceProject, targetProject, targetBranch, children}) {

    const context = {
        sourceProject,
        targetProject,
        targetBranch,
    }

    return (
        <AutoVersioningAuditContext.Provider value={context}>
            {children}
        </AutoVersioningAuditContext.Provider>
    )

}