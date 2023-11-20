import {createContext, useState} from "react";

export const WidgetExpansionContext = createContext({})

export default function WidgetExpansionContextProvider({children}) {

    const [expansion, setExpansion] = useState({
        uuid: ''
    })

    const toggleExpansion = (uuid) => {
        setExpansion({
            uuid: expansion.uuid === uuid ? '' : uuid,
        })
    }

    return <WidgetExpansionContext.Provider value={{expansion, toggleExpansion}}>{children}</WidgetExpansionContext.Provider>
}
