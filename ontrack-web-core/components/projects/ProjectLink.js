import Link from "next/link";
import {projectUri} from "@components/common/Links";

export default function ProjectLink({project, text}) {
    return (
        <>
            <Link href={projectUri(project)}>{text ? text : project.name}</Link>
        </>
    )
}