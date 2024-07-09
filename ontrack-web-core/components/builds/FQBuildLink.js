import ProjectLink from "@components/projects/ProjectLink";
import {Space, Typography} from "antd";
import BranchLink from "@components/branches/BranchLink";
import BuildLink from "@components/builds/BuildLink";

export default function FQBuildLink({build}) {
    return (
        <>
            <Space size={4}>
                <ProjectLink project={build.branch.project}/>
                <Typography.Text type="secondary">/</Typography.Text>
                <BranchLink branch={build.branch}/>
                <Typography.Text type="secondary">/</Typography.Text>
                <BuildLink build={build}/>
            </Space>
        </>
    )
}