import {createContext} from "react";

export const AutoVersioningAuditContext = createContext({
    sourceProject: undefined,
    targetProject: undefined,
    targetBranch: undefined,
})

export default function AutoVersioningAuditContextProvider({sourceProject, children}) {

    const context = {
        sourceProject,
    }

    return (
        <AutoVersioningAuditContext.Provider value={context}>
            {children}
        </AutoVersioningAuditContext.Provider>
    )

}