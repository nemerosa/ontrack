import {FaRegThumbsUp} from "react-icons/fa";
import {Popover} from "antd";
import BuildPromoteDialog, {useBuildPromoteDialog} from "@components/builds/BuildPromoteDialog";

export default function BuildPromoteAction({build, promotionLevel, tooltip, onPromotion}) {
    const actualTooltip = tooltip ? tooltip : `Promotes the build to ${promotionLevel.name}`

    const dialog = useBuildPromoteDialog({
        onSuccess: onPromotion,
    })

    const onPromote = () => {
        dialog.start({
            build,
            promotionLevel,
        })
    }

    return (
        <>
            <Popover content={actualTooltip}>
                <FaRegThumbsUp data-testid={`build-promote-${build.id}-${promotionLevel.id}`} className="ot-command" onClick={onPromote}/>
            </Popover>
            <BuildPromoteDialog buildPromoteDialog={dialog}/>
        </>
    )
}