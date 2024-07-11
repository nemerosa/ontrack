import {createContext, useState} from "react";

export const SearchContext = createContext({active: false})

export default function SearchContextProvider({children}) {

    const [active, setActive] = useState(false)

    const context = {
        active,
        setActive,
    }

    return <SearchContext.Provider value={context}>{children}</SearchContext.Provider>
}