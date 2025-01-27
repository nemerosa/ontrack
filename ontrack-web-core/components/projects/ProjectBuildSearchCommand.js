import {Command} from "@components/common/Commands";
import {FaSearch} from "react-icons/fa";
import {projectBuildSearchUri} from "@components/common/Links";

export default function ProjectBuildSearchCommand({id}) {
    return (
        <>
            <Command
                icon={<FaSearch/>}
                text="Search builds"
                title="Looking for builds in this project based on several criteria"
                href={projectBuildSearchUri({id})}
            />
        </>
    )
}