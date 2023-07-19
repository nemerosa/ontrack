import {Drawer} from "antd";
import {useContext} from "react";
import {BranchViewContext} from "@components/branches/BranchViewContext";

export default function BranchInfoViewDrawer() {

    const {infoView} = useContext(BranchViewContext)

    return <Drawer
        title="Branch information"
        placement="right"
        open={infoView.infoViewExpanded}
        onClose={infoView.toggleInfoView}
    >
        TODO Branch info here
    </Drawer>
}