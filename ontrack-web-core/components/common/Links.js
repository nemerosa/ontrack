import Link from "next/link";

export function projectLink(project, text) {
    return <Link href={`/ui/project/${project.id}`}>{text ? text : project.name}</Link>
}