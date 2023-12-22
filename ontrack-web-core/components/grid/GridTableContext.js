import {createContext, useState} from "react";

export const GridTableContext = createContext({
    expandable: false,
    draggable: false,
    setExpandable: (flag) => {
    },
    expandedId: '',
    toggleExpandedId: (id) => {
    },
    clearExpandedId: () => {
    },
})

export default function GridTableContextProvider({isExpandable = true, isDraggable = true, children}) {

    const [expandable, setExpandable] = useState(isExpandable)
    const [draggable, setDraggable] = useState(isDraggable)
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
        draggable,
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