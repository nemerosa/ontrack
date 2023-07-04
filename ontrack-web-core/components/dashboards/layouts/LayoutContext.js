import {createContext, useContext, useEffect, useState} from "react";
import {DashboardContext} from "@components/dashboards/DashboardPage";

export const LayoutContext = createContext({widgets: []})

const LayoutContextProvider = ({children}) => {

    const dashboard = useContext(DashboardContext)
    const [widgets, setWidgets] = useState([])

    useEffect(() => {
        if (dashboard) {
            setWidgets(dashboard.widgets)
        }
    }, [dashboard])

    return <LayoutContext.Provider value={widgets}>{children}</LayoutContext.Provider>
}

export default LayoutContextProvider
