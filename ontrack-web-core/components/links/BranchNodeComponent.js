import {Flex, Space, Tooltip, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";
import BranchDisplayNameLink from "@components/links/BranchDisplayNameLink";
import {FaArrowCircleLeft, FaCompressArrowsAlt, FaLink, FaProjectDiagram} from "react-icons/fa";
import {NodeSection} from "@components/links/NodeSection";
import BuildRef from "@components/links/BuildRef";
import Link from "next/link";
import {branchUri, buildLinksUri} from "@components/common/Links";
import Timestamp from "@components/common/Timestamp";
import BuildPromotions from "@components/links/BuildPromotions";

export default function BranchNodeComponent({branch, focused, onToggleFocus}) {

    // noinspection JSFileReferences
    const linkToBranchLinks = <Link
        href={`${branchUri(branch)}/links`}
        title="Links for this branch"
    ><FaLink/></Link>

    const toggleFocus = (event) => {
        event.stopPropagation()
        if (onToggleFocus) {
            onToggleFocus({id: Number(branch.id)})
        }
    }
    const latestBuild = branch.latestBuilds ? branch.latestBuilds[0] : undefined

    return (
        <>
            <Space direction="vertical" className="ot-line">
                <Typography.Text>
                    {branch && <ProjectLink project={branch.project} shorten={true}/>}
                </Typography.Text>
                <Flex justify="space-between" align="center" style={{width: '100%'}}>
                    <BranchDisplayNameLink branch={branch}>
                        &nbsp;
                        {linkToBranchLinks}
                    </BranchDisplayNameLink>
                    {
                        onToggleFocus &&
                        <Tooltip title={focused ? "Restores the full graph" : "Focus on this project"}>
                            <span style={{marginLeft: '8px'}}>
                                <FaCompressArrowsAlt
                                    color={focused ? 'blue' : undefined}
                                    onClick={toggleFocus}
                                />
                            </span>
                        </Tooltip>
                    }
                </Flex>
                {
                    latestBuild &&
                    <NodeSection
                        icon={<FaArrowCircleLeft/>}
                        title="Latest build"
                    >
                        <Space>
                            <BuildRef build={latestBuild}/>
                            <Tooltip title="Graph of build links">
                                <Link href={buildLinksUri(latestBuild)}><FaProjectDiagram size="12"/></Link>
                            </Tooltip>
                        </Space>
                        <Timestamp value={latestBuild?.creation?.time}/>
                        <BuildPromotions build={latestBuild}/>
                    </NodeSection>
                }
            </Space>
        </>
    )
}