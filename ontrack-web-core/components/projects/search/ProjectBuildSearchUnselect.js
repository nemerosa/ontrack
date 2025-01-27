import InlineCommand from "@components/common/InlineCommand";
import {FaMinus} from "react-icons/fa";

export default function ProjectBuildSearchUnselect({build, onBuildUnselected}) {

    const onClick = () => {
        if (onBuildUnselected) {
            onBuildUnselected(build)
        }
    }

    return (
        <>
            {
                <InlineCommand
                    icon={<FaMinus/>}
                    title="Unselect this build as a boundary for a change log"
                    onClick={onClick}
                />
            }
        </>
    )
}