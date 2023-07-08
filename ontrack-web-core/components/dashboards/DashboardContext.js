import {createContext} from "react";

export const DashboardContext = createContext({
    dashboards: [],
    selectedDashboard: undefined,
})
// export const DashboardDispatchContext = createContext(null)
