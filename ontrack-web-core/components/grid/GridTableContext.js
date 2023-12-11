import {createContext, useState} from "react";

export const GridTableContext = createContext({
    expandable: false,
    setExpandable: (flag) => {
    },
    expandedId: '',
    toggleExpandedId: (id) => {
    },
    clearExpandedId: () => {
    },
})

export default function GridTableContextProvider({children}) {

    const [expandable, setExpandable] = useState(true)
    const [expandedId, setExpandedId] = useState('')

    const toggleExpandedId = (id) => {
        if (expandable && id) {
            if (expandedId === id) {
                setExpandedId('')
            } else {
                setExpandedId(id)
            }
        }
    }

    const clearExpandedId = () => {
        setExpandedId('')
    }

    const context = {
        expandable,
        setExpandable,
        expandedId,
        toggleExpandedId,
        clearExpandedId,
    }

    return (
        <GridTableContext.Provider value={context}>
            {children}
        </GridTableContext.Provider>
    )

}