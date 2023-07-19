import {useContext} from "react";
import {FloatButton} from "antd";
import {FaInfoCircle} from "react-icons/fa";
import {BranchViewContext} from "@components/branches/BranchViewContext";

export default function BranchInfoViewExpandButton() {

    const {infoView} = useContext(BranchViewContext)

    return (
        <>
            {
                !infoView.infoViewExpanded && <FloatButton
                    icon={<FaInfoCircle/>}
                    tooltip="Displays information about the branch"
                    onClick={infoView.toggleInfoView}
                />
            }
        </>
    )
}