import Link from "next/link";
import {projectUri} from "@components/common/Links";
import ShortenedName from "@components/common/ShortenedName";

export default function ProjectLink({project, text, shorten}) {
    return (
        <>
            <Link href={projectUri(project)} className={project.disabled ? "ot-entity-disabled" : ""}>
                {
                    text ?? (
                        shorten ? <ShortenedName text={project.name}/> : project.name
                    )
                }
            </Link>
        </>
    )
}