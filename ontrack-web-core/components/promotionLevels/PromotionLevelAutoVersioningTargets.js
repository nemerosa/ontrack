import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {Space, Table, Typography} from "antd";
import {gql} from "graphql-request";
import ProjectLink from "@components/projects/ProjectLink";
import BranchLink from "@components/branches/BranchLink";
import AutoVersioningApproval from "@components/extension/auto-versioning/AutoVersioningApproval";
import Link from "next/link";
import {branchAutoVersioningUri} from "@components/common/Links";
import {FaCog} from "react-icons/fa";
import CheckStatus from "@components/common/CheckStatus";

const {Column} = Table

export default function PromotionLevelAutoVersioningTargets({promotionLevel}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const [targets, setTargets] = useState([])

    useEffect(() => {
        if (client && promotionLevel) {
            setLoading(true)
            client.request(
                gql`
                    query GetPromotionLevelAVTargets($id: Int!) {
                        promotionLevel(id: $id) {
                            autoVersioningTrail {
                                potentialTargetBranches {
                                    id
                                    name
                                    displayName
                                    project {
                                        id
                                        name
                                    }
                                    autoVersioningConfig {
                                        configurations {
                                            autoApproval
                                            autoApprovalMode
                                            targetPath
                                        }
                                    }
                                }
                                rejectedTargetBranches {
                                    branch {
                                        id
                                    }
                                    reason
                                }
                            }
                        }
                    }
                `,
                {
                    id: promotionLevel.id,
                }
            ).then(data => {
                // Flattening the list to the configuration level
                const targets = []
                const trail = data.promotionLevel.autoVersioningTrail
                const rejectionReasons = {}
                trail.rejectedTargetBranches.forEach(rejectedBranch => {
                    rejectionReasons[rejectedBranch.branch.id] = rejectedBranch.reason
                })
                trail.potentialTargetBranches.forEach(potentialTargetBranch => {
                    potentialTargetBranch.rejectionReason = rejectionReasons[potentialTargetBranch.id]
                    if (potentialTargetBranch.autoVersioningConfig.configurations) {
                        potentialTargetBranch.autoVersioningConfig.configurations.forEach(config => {
                            targets.push({
                                branch: potentialTargetBranch,
                                config,
                            })
                        })
                    }
                })
                setTargets(targets)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, promotionLevel])

    return (
        <>
            <Table
                loading={loading}
                dataSource={targets}
                pagination={false}
            >

                <Column
                    key="branch"
                    title="Target branch"
                    render={(_, target) => (
                        <>
                            {
                                target.branch && <Space>
                                    <ProjectLink project={target.branch.project}/>/<BranchLink branch={target.branch}/>
                                    {
                                        target.config &&
                                        <Link href={branchAutoVersioningUri(target.branch)}
                                              title="Auto-versioning config"><FaCog/></Link>
                                    }
                                </Space>
                            }
                        </>
                    )}
                />

                <Column
                    key="state"
                    title="State"
                    render={(_, target) => (
                        <>
                            <Space direction="vertical">
                                <CheckStatus
                                    value={!target.branch.rejectionReason}
                                    text="Eligible"
                                    noText="Not eligible"
                                />
                                {
                                    target.branch.rejectionReason &&
                                    <Typography.Text type="secondary">{target.branch.rejectionReason}</Typography.Text>
                                }
                            </Space>
                        </>
                    )}
                />

                <Column
                    key="approval"
                    title="Approval"
                    render={(_, {config}) =>
                        <>
                            {
                                config &&
                                <AutoVersioningApproval
                                    autoApproval={config.autoApproval}
                                    autoApprovalMode={config.autoApprovalMode}
                                />
                            }
                        </>
                    }
                />

                <Column
                    key="targetPath"
                    title="Target path"
                    render={(_, {config}) =>
                        <>
                            {
                                config &&
                                <Typography.Text code>{config.targetPath}</Typography.Text>
                            }
                        </>
                    }
                />

            </Table>
        </>
    )
}