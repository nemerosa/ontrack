import {Space, Typography} from "antd";
import {FaArrowRight, FaDatabase} from "react-icons/fa";
import Link from "next/link";
import ProjectLink from "@components/projects/ProjectLink";

export default function SCMCatalogEntrySummary({entry, project}) {
    return (
        <>
            <Space>
                <FaDatabase/>
                <Link href={entry.repositoryPage}>{entry.repository}</Link>
                [<Typography.Text type="secondary">{entry.scm}</Typography.Text>]
                {
                    project &&
                    <>
                        <FaArrowRight/>
                        <ProjectLink project={project}/>
                    </>
                }
            </Space>
        </>
    )
}