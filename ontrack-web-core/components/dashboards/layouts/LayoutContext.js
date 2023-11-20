import {createContext, useContext, useEffect, useState} from "react";
import {DashboardContext} from "@components/dashboards/DashboardContext";

export const LayoutContext = createContext({widgets: []})

const LayoutContextProvider = ({children}) => {

    const {selectedDashboard} = useContext(DashboardContext)
    const [widgets, setWidgets] = useState([])

    useEffect(() => {
        if (selectedDashboard) {
            setWidgets(selectedDashboard.widgets)
        }
    }, [selectedDashboard])

    return <LayoutContext.Provider value={widgets}>{children}</LayoutContext.Provider>
}

export default LayoutContextProvider
