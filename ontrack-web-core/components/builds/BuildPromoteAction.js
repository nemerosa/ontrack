import {FaRegThumbsUp} from "react-icons/fa";
import {Popover} from "antd";
import BuildPromoteDialog, {useBuildPromoteDialog} from "@components/builds/BuildPromoteDialog";

export default function BuildPromoteAction({build, promotionLevel, tooltip}) {
    const actualTooltip = tooltip ? tooltip : `Promotes the build to ${promotionLevel.name}`

    const dialog = useBuildPromoteDialog()

    const onPromote = () => {
        dialog.start({
            build,
            promotionLevel
        })
    }

    return (
        <>
            <Popover content={actualTooltip}>
                <FaRegThumbsUp className="ot-command" onClick={onPromote}/>
            </Popover>
            <BuildPromoteDialog buildPromoteDialog={dialog}/>
        </>
    )
}