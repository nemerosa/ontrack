import {Space, Typography} from "antd";
import {FaLink, FaMagic} from "react-icons/fa";
import AutoVersioningInfo from "@components/extension/auto-versioning/AutoVersioningInfo";
import {NodeSection} from "@components/links/NodeSection";
import LatestLinkInfo from "@components/links/LatestLinkInfo";
import ProjectLink from "@components/projects/ProjectLink";

export default function BranchLinkNodeComponent({link, sourceBranch, targetBranch}) {

    const {qualifier, sourceBuild, targetBuild, autoVersioning} = link

    const sourceBranchLatest = sourceBranch.latestBuilds ? sourceBranch.latestBuilds[0] : undefined
    const targetBranchLatest = targetBranch.latestBuilds ? targetBranch.latestBuilds[0] : undefined

    const latestOk = sourceBranchLatest && targetBranchLatest &&
        (sourceBranchLatest.id === sourceBuild.id) &&
        (targetBranchLatest.id === targetBuild.id)

    return (
        <Space direction="vertical">
            <Space>
                <FaLink/>
                <ProjectLink project={targetBuild.branch.project}/>
                {
                    qualifier &&
                    <Typography.Text>{qualifier}</Typography.Text>
                }
            </Space>
            {/* Latest link */}
            <LatestLinkInfo
                sourceBuild={sourceBuild}
                latestOk={latestOk}
                targetBuild={targetBuild}
            />
            {/* AV information */}
            {
                autoVersioning &&
                <NodeSection
                    icon={<FaMagic/>}
                    title="Auto versioning"
                >
                    <AutoVersioningInfo
                        autoVersioning={autoVersioning}
                        branchLink={link}
                    />
                </NodeSection>
            }
        </Space>
    )
}