import {createContext, useState} from "react";

export const StoredGridLayoutContext = createContext({
    resetLayoutCount: 0,
    resetLayout: () => {
    },
})

export default function StoredGridLayoutContextProvider({children}) {
    const [resetLayoutCount, setResetLayoutCount] = useState(0)
    const resetLayout = () => {
        setResetLayoutCount(resetLayoutCount + 1)
    }
    const context = {
        resetLayoutCount,
        resetLayout,
    }
    return (
        <>
            <StoredGridLayoutContext.Provider value={context}>
                {children}
            </StoredGridLayoutContext.Provider>
        </>
    )
}