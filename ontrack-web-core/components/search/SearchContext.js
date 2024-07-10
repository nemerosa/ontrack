import {createContext, useState} from "react";

export const SearchContext = createContext({q: ''})

export default function SearchContextProvider({children}) {

    const [q, setQ] = useState('')

    const context = {
        q,
        setQ,
    }

    return <SearchContext.Provider value={context}>{children}</SearchContext.Provider>
}