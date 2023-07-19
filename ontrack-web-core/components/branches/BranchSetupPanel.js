import {useContext} from "react";
import {BranchViewContext} from "@components/branches/BranchViewContext";
import {Button, Space, Tooltip} from "antd";
import {FaAngleDoubleDown, FaAngleDoubleUp} from "react-icons/fa";

export default function BranchSetupPanel() {

    const {setupPanel} = useContext(BranchViewContext)

    return (
        <>
            <div
                style={{
                    padding: 8,
                    border: "solid 1px black",
                    borderRadius: 8,
                }}
                className="ot-line"
            >
                {/* Setup collapsed */}
                {
                    !setupPanel.setupPanelExpanded && <Space>
                        <Tooltip title="Displays the options for the branch view setup" placement="bottomRight">
                            <Button icon={<FaAngleDoubleDown/>} onClick={setupPanel.toggleSetupPanel}/>
                        </Tooltip>
                    </Space>
                }
                {/* Setup expanded */}
                {
                    setupPanel.setupPanelExpanded && <Space>
                        <Tooltip title="Collapses the options for the branch view setup" placement="bottomRight">
                            <Button icon={<FaAngleDoubleUp/>} onClick={setupPanel.toggleSetupPanel}/>
                            Setup!!
                        </Tooltip>
                    </Space>
                }
            </div>
        </>
    )
}