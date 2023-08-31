import {createContext, useState} from "react";
import {useRange} from "@components/common/RangeSelector";

export const BranchViewContext = createContext({
    setupPanel: {
        setupPanelExpanded: false,
        toggleSetupPanel: () => {

        },
    },
    buildRange: {
        range: {first: undefined, second: undefined},
        onRangeChange: () => {
        }
    },
})

export const BranchViewContextProvider = ({children}) => {

    const [setupPanelExpanded, setSetupPanelExpanded] = useState(false)

    const toggleSetupPanel = () => {
        setSetupPanelExpanded(!setupPanelExpanded)
    }

    const buildRange = useRange()

    const context = {
        setupPanel: {
            setupPanelExpanded,
            toggleSetupPanel,
        },
        buildRange,
    }

    return <BranchViewContext.Provider value={context}>{children}</BranchViewContext.Provider>

}