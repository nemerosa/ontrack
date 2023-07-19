import {createContext, useState} from "react";
import {useRange} from "@components/common/RangeSelector";

export const BranchViewContext = createContext({
    infoView: {
        infoViewExpanded: false,
        toggleInfoView: () => {
        },
    },
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

    const [infoViewExpanded, setInfoViewExpanded] = useState(false)
    const [setupPanelExpanded, setSetupPanelExpanded] = useState(false)

    const toggleInfoView = () => {
        setInfoViewExpanded(!infoViewExpanded)
    }

    const toggleSetupPanel = () => {
        setSetupPanelExpanded(!setupPanelExpanded)
    }

    const buildRange = useRange()

    const context = {
        infoView: {
            infoViewExpanded,
            toggleInfoView,
        },
        setupPanel: {
            setupPanelExpanded,
            toggleSetupPanel,
        },
        buildRange,
    }

    return <BranchViewContext.Provider value={context}>{children}</BranchViewContext.Provider>

}