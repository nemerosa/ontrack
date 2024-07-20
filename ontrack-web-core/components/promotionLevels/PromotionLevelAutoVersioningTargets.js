import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {Table, Typography} from "antd";
import {gql} from "graphql-request";
import ProjectLink from "@components/projects/ProjectLink";
import BranchLink from "@components/branches/BranchLink";
import AutoVersioningApproval from "@components/extension/auto-versioning/AutoVersioningApproval";

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
                            autoVersioningTargets {
                                branch {
                                    id
                                    name
                                    displayName
                                    project {
                                        id
                                        name
                                    }
                                }
                                configurations {
                                    autoApproval
                                    autoApprovalMode
                                    targetPath
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
                data.promotionLevel.autoVersioningTargets.forEach(target => {
                    target.configurations.forEach(config => {
                        targets.push({
                            branch: target.branch,
                            config,
                        })
                    })
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
                                target.branch && <>
                                    <ProjectLink project={target.branch.project}/>/<BranchLink branch={target.branch}/>
                                </>
                            }
                        </>
                    )}
                />

                <Column
                    key="approval"
                    title="Approval"
                    render={(_, {config}) =>
                        <>
                            <AutoVersioningApproval
                                autoApproval={config.autoApproval}
                                autoApprovalMode={config.autoApprovalMode}
                            />
                        </>
                    }
                />

                <Column
                    key="targetPath"
                    title="Target path"
                    render={(_, {config}) =>
                        <>
                            <Typography.Text code>{config.targetPath}</Typography.Text>
                        </>
                    }
                />

            </Table>
        </>
    )
}