import InlineCommand from "@components/common/InlineCommand";
import {FaPlus} from "react-icons/fa";

export default function ProjectBuildSearchSelect({build, buildSelectable, onBuildSelected}) {

    const onClick = () => {
        if (buildSelectable && onBuildSelected) {
            onBuildSelected(build)
        }
    }

    return (
        <>
            {
                buildSelectable && !build.selected &&
                <InlineCommand
                    icon={<FaPlus/>}
                    title="Select this build as a boundary for a change log"
                    onClick={onClick}
                />
            }
        </>
    )
}