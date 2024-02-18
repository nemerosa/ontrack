import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {Space, Spin, Table, Typography} from "antd";
import AutoVersioningAuditEntryTarget from "@components/extension/auto-versioning/AutoVersioningAuditEntryTarget";
import {FaSquare} from "react-icons/fa";
import AutoVersioningAuditEntryState from "@components/extension/auto-versioning/AutoVersioningAuditEntryState";

const {Column} = Table

export default function AutoVersioningAuditView() {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const [entries, setEntries] = useState([])

    const [pagination, setPagination] = useState({
        offset: 0,
        size: 20,
    })

    const [pageInfo, setPageInfo] = useState({})

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query AutoVersioningAudit(
                        $offset: Int!,
                        $size: Int!,
                    ) {
                        autoVersioningAuditEntries(
                            offset: $offset,
                            size: $size,
                        ) {
                            pageInfo {
                                nextPage {
                                    offset
                                    size
                                }
                            }
                            pageItems {
                                mostRecentState {
                                    creation {
                                        time
                                    }
                                    state
                                    data
                                }
                                duration
                                running
                                audit {
                                    creation {
                                        time
                                    }
                                    state
                                    data
                                }
                                routing
                                queue
                                order {
                                    uuid
                                    sourceProject
                                    branch {
                                        name
                                        project {
                                            id
                                            name
                                        }
                                    }
                                    repositoryHtmlURL
                                    targetPaths
                                    targetRegex
                                    targetProperty
                                    targetPropertyRegex
                                    targetPropertyType
                                    targetVersion
                                    autoApproval
                                    autoApprovalMode
                                    upgradeBranchPattern
                                    postProcessing
                                    postProcessingConfig
                                    validationStamp
                                }
                            }
                        }
                    }
                `,
                {
                    offset: pagination.offset,
                    size: pagination.size,
                }
            )
            .then(data => {
                setPageInfo(data.autoVersioningAuditEntries.pageInfo)
                setEntries(data.autoVersioningAuditEntries.pageItems)
            })
            .finally(() => {
                setLoading(false)
            })
        }
    }, [client])

    return (
        <>
            <Space className="ot-line" direction="vertical">
                {/* TODO Filter form */}

                {/* List */}
                <Table
                    dataSource={entries}
                    loading={loading}
                    pagination={false}
                >

                    <Column
                        key="target"
                        title="Target"
                        render={(_, entry) =>
                            <AutoVersioningAuditEntryTarget entry={entry}/>
                        }
                    />

                    <Column
                        key="source"
                        title="Source project"
                        render={(_, entry) =>
                            <Typography.Text>{entry.order.sourceProject}</Typography.Text>
                        }
                    />

                    <Column
                        key="version"
                        title="Version"
                        render={(_, entry) =>
                            <Typography.Text>{entry.order.targetVersion}</Typography.Text>
                        }
                    />

                    <Column
                        key="post-processing"
                        title="Post processing"
                        render={(_, entry) =>
                            <>
                                {
                                    entry.order.postProcessing &&
                                    <Typography.Text>{entry.order.postProcessing}</Typography.Text>
                                }
                                {
                                    !entry.order.postProcessing &&
                                    <Typography.Text disabled italic>None</Typography.Text>
                                }
                            </>
                        }
                    />

                    <Column
                        key="approval"
                        title="Approval"
                        render={(_, entry) =>
                            <>
                                {
                                    entry.order.autoApproval &&
                                    <Typography.Text>{entry.order.autoApprovalMode}</Typography.Text>
                                }
                                {
                                    !entry.order.autoApproval &&
                                    <Typography.Text disabled italic>Manual</Typography.Text>
                                }
                            </>
                        }
                    />

                    <Column
                        key="running"
                        title="Running"
                        render={(_, entry) =>
                            <>
                                {
                                    entry.running &&
                                    <Space>
                                        <Spin size="small"/>
                                        Running
                                    </Space>
                                }
                                {
                                    !entry.running &&
                                    <Typography.Text disabled>
                                        <Space>
                                            <FaSquare/>
                                            Finished
                                        </Space>
                                    </Typography.Text>
                                }
                            </>
                        }
                    />

                    <Column
                        key="state"
                        title="State"
                        render={(_, entry) =>
                            <AutoVersioningAuditEntryState entry={entry}/>
                        }
                    />

                    {/* TODO PR */}
                    {/* TODO Queuing info */}
                    {/* TODO Timestamp */}
                    {/* TODO Duration */}
                    {/* TODO Details buttons */}
                    {/* TODO Showing the details */}

                </Table>

                {/* TODO Pagination (more) */}

            </Space>
        </>
    )
}