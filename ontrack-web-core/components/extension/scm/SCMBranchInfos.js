import {useEffect, useState} from "react";
import {Space, Tag, Tree, Typography} from "antd";
import {FaCodeBranch, FaDotCircle, FaTag} from "react-icons/fa";
import BranchLink from "@components/branches/BranchLink";
import BuildLink from "@components/builds/BuildLink";
import ReleaseDecoration from "@components/builds/ReleaseDecoration";
import PromotionLevelLink from "@components/promotionLevels/PromotionLevelLink";
import TimestampText from "@components/common/TimestampText";

export default function SCMBranchInfos({branchInfos}) {

    const [treeData, setTreeData] = useState([])
    const [nodeKeys, setNodeKeys] = useState([])

    useEffect(() => {
        // Branch types as roots
        const roots = []
        const keys = []
        branchInfos.forEach(({type, branchInfoList}) => {
            const typeNodeKey = `type-${type}`;
            const typeNode = {
                key: typeNodeKey,
                title: <Space>
                    <FaTag/>
                    <Typography.Text strong>{type}</Typography.Text>
                </Space>,
                children: []
            }
            roots.push(typeNode)
            keys.push(typeNodeKey)

            // Branches as second level
            branchInfoList.forEach(branchInfo => {
                const branchNodeKey = `branch-${branchInfo.branch.id}`;
                const branchNode = {
                    key: branchNodeKey,
                    title: <Space>
                        <FaCodeBranch/>
                        <Typography.Text strong>Branch</Typography.Text>
                        <BranchLink branch={branchInfo.branch}/>
                        {
                            branchInfo.branch.disabled &&
                            <Tag color="gray">Disabled</Tag>
                        }
                    </Space>,
                    children: [],
                }
                typeNode.children.push(branchNode)
                keys.push(branchNodeKey)

                // First build as first child
                if (branchInfo.firstBuild) {
                    const buildNodeKey = `build-${branchInfo.firstBuild.id}`;
                    const buildNode = {
                        key: buildNodeKey,
                        title: <Space>
                            <FaDotCircle/>
                            <Typography.Text strong>First build</Typography.Text>
                            <BuildLink build={branchInfo.firstBuild}/>
                            {
                                branchInfo.firstBuild.displayName &&
                                branchInfo.firstBuild.displayName !== branchInfo.firstBuild.name &&
                                <ReleaseDecoration value={branchInfo.firstBuild.displayName}/>
                            }
                            <Typography.Text type="secondary"><TimestampText value={branchInfo.firstBuild.creation.time}/></Typography.Text>
                        </Space>,
                    }
                    branchNode.children.push(buildNode)
                }

                // Promotions
                branchInfo.promotions.forEach(promotion => {
                    const promotionNodeKey = `promotion-${promotion.id}`;
                    const promotionNode = {
                        key: promotionNodeKey,
                        title: <Space>
                            <PromotionLevelLink promotionLevel={promotion.promotionLevel} size={16}/>
                            <Typography.Text>for build</Typography.Text>
                            <BuildLink build={promotion.build}/>
                            {
                                promotion.build.displayName &&
                                promotion.build.displayName !== promotion.build.name &&
                                <ReleaseDecoration value={promotion.build.displayName}/>
                            }
                            <Typography.Text type="secondary"><TimestampText value={promotion.creation.time}/></Typography.Text>
                        </Space>,
                    }
                    branchNode.children.push(promotionNode)
                })

            })
        })
        setTreeData(roots)
        setNodeKeys(keys)
    }, [branchInfos])

    return (
        <>
            <Tree
                treeData={treeData}
                expandedKeys={nodeKeys}
                onExpand={() => {}}
                expandAction={false}
                switcherIcon={null}
                selectable={false}
            />
        </>
    )
}