import {Tooltip} from "antd";
import Link from "next/link";
import {FaLevelDownAlt} from "react-icons/fa";

export default function AutoVersioningAuditBranchTargetLink({branch}) {

    return (
        <>
            <Tooltip title={`Auto-versioning audit for branch target ${branch.project.name}/${branch.name}`}>
                <Link href={`/extension/auto-versioning/audit-branch-target/${branch.id}`}>
                    <FaLevelDownAlt/>
                </Link>
            </Tooltip>
        </>
    )
}