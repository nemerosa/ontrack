import {Space, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";
import BuildLink from "@components/builds/BuildLink";
import TimestampText from "@components/common/TimestampText";
import BuildPromotions from "@components/links/BuildPromotions";
import BranchDisplayNameLink from "@components/links/BranchDisplayNameLink";

export default function BuildLinksTreeNode({build, qualifier}) {
    return (
        <>
            <Space>
                {/* Project name */}
                <ProjectLink project={build.branch.project}/>
                {/* Branch name */}
                <Typography.Text type="secondary">/</Typography.Text>
                <BranchDisplayNameLink branch={build.branch}/>
                {/* Build name */}
                <Typography.Text type="secondary">/</Typography.Text>
                <Typography.Text strong>
                    <BuildLink build={build} displayTooltip={true}></BuildLink>
                </Typography.Text>
                {/* Qualifier */}
                {
                    qualifier && <Typography.Text type="secondary">[{qualifier}]</Typography.Text>
                }
                {/* Creation time */}
                <Typography.Text type="secondary">
                    <TimestampText value={build.creation.time}/>
                </Typography.Text>
                {/* Promotions */}
                <BuildPromotions build={build}/>
            </Space>
        </>
    )
}