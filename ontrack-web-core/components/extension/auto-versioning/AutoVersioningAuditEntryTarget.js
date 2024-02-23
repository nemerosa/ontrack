import ProjectLink from "@components/projects/ProjectLink";
import Link from "next/link";
import {FaExternalLinkAlt} from "react-icons/fa";
import {Space, Typography} from "antd";
import BranchLink from "@components/branches/BranchLink";
import AutoVersioningAuditProjectSourceLink
    from "@components/extension/auto-versioning/AutoVersioningAuditProjectSourceLink";
import AutoVersioningAuditProjectTargetLink
    from "@components/extension/auto-versioning/AutoVersioningAuditProjectTargetLink";
import AutoVersioningAuditBranchTargetLink
    from "@components/extension/auto-versioning/AutoVersioningAuditBranchTargetLink";

export default function AutoVersioningAuditEntryTarget({entry, auditLink}) {
    return (
        <Space>
            <ProjectLink project={entry.order.branch.project}/>
            {
                entry.order.repositoryHtmlURL &&
                <Link href={entry.order.repositoryHtmlURL}>
                    <FaExternalLinkAlt/>
                </Link>
            }
            {
                auditLink &&
                <>
                    <AutoVersioningAuditProjectSourceLink name={entry.order.sourceProject}/>
                    <AutoVersioningAuditProjectTargetLink project={entry.order.branch.project}/>
                </>
            }
            <Typography.Text>/</Typography.Text>
            <BranchLink branch={entry.order.branch}/>
            {
                auditLink &&
                <>
                    <AutoVersioningAuditBranchTargetLink branch={entry.order.branch}/>
                </>
            }
        </Space>
    )
}