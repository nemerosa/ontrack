import {Tooltip} from "antd";
import Link from "next/link";
import {FaLevelDownAlt} from "react-icons/fa";

export default function AutoVersioningAuditProjectTargetLink({project}) {

    return (
        <>
            <Tooltip title={`Auto-versioning audit for project target ${project.name}`}>
                <Link href={`/extension/auto-versioning/audit-project-target/${project.id}`}>
                    <FaLevelDownAlt/>
                </Link>
            </Tooltip>
        </>
    )
}