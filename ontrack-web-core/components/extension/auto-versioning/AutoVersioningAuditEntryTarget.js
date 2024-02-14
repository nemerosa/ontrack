import ProjectLink from "@components/projects/ProjectLink";
import Link from "next/link";
import {FaExternalLinkAlt} from "react-icons/fa";
import {Space, Typography} from "antd";
import BranchLink from "@components/branches/BranchLink";

export default function AutoVersioningAuditEntryTarget({entry}) {
    return (
        <Space>
            <ProjectLink project={entry.order.branch.project}/>
            {
                entry.order.repositoryHtmlURL &&
                <Link href={entry.order.repositoryHtmlURL}>
                    <FaExternalLinkAlt/>
                </Link>
            }
            <Typography.Text>/</Typography.Text>
            <BranchLink branch={entry.order.branch}/>
        </Space>
    )
}