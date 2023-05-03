import Link from "next/link";

const prefix = '';

export function projectLink(project, text) {
    return <Link href={`${prefix}/project/${project.id}`}>{text ? text : project.name}</Link>
}