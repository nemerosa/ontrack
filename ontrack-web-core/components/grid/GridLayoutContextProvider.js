import {createContext, useState} from "react";

export const GridLayoutContext = createContext({
    resetLayoutCount: 0,
    resetLayout: () => {
    },
})

export default function GridLayoutContextProvider({children}) {

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
            <GridLayoutContext.Provider value={context}>
                {children}
            </GridLayoutContext.Provider>
        </>
    )
}