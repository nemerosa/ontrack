import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {Button, Popover, Space, Spin, Table, Tooltip, Typography} from "antd";
import AutoVersioningAuditEntryTarget from "@components/extension/auto-versioning/AutoVersioningAuditEntryTarget";
import {FaSearch, FaSquare} from "react-icons/fa";
import AutoVersioningAuditEntryState from "@components/extension/auto-versioning/AutoVersioningAuditEntryState";
import AutoVersioningAuditEntryPR from "@components/extension/auto-versioning/AutoVersioningAuditEntryPR";
import AutoVersioningAuditEntryQueuing from "@components/extension/auto-versioning/AutoVersioningAuditEntryQueuing";
import TimestampText from "@components/common/TimestampText";
import Duration from "@components/common/Duration";
import Link from "next/link";

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
                if (pagination.offset > 0) {
                    setEntries([...entries, ...data.autoVersioningAuditEntries.pageItems])
                } else {
                    setEntries(data.autoVersioningAuditEntries.pageItems)
                }
            })
            .finally(() => {
                setLoading(false)
            })
        }
    }, [client, pagination])

    const onLoadMore = () => {
        if (pageInfo.nextPage) {
            setPagination(pageInfo.nextPage)
        }
    }

    return (
        <>
            <Space className="ot-line" direction="vertical">
                {/* TODO Filter form */}

                {/* List */}
                <Table
                    dataSource={entries}
                    loading={loading}
                    pagination={false}
                    size="small"
                    footer={() => (
                        <>
                            <Space>
                                <Popover
                                    content={
                                        (pageInfo && pageInfo.nextPage) ?
                                            "There are more entries to be loaded" :
                                            "There are no more entries to be loaded"
                                    }
                                >
                                    <Button
                                        onClick={onLoadMore}
                                        disabled={!pageInfo || !pageInfo.nextPage}
                                    >
                                        <Space>
                                            <FaSearch/>
                                            <Typography.Text>Load more...</Typography.Text>
                                        </Space>
                                    </Button>
                                </Popover>
                            </Space>
                        </>
                    )}
                >

                    <Column
                        key="uuid"
                        title="UUID"
                        render={(_, entry) =>
                            <Tooltip title="Displays the details of this entry into a separate page.">
                                <Link
                                    href={`/extension/auto-versioning/audit/detail/${entry.order.uuid}`}>{entry.order.uuid}</Link>
                            </Tooltip>
                        }
                    />

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
                            <AutoVersioningAuditEntryState status={entry.mostRecentState}/>
                        }
                    />

                    <Column
                        key="pr"
                        title="PR"
                        render={(_, entry) =>
                            <AutoVersioningAuditEntryPR entry={entry}/>
                        }
                    />

                    <Column
                        key="queuing"
                        title="Queuing"
                        render={(_, entry) =>
                            <AutoVersioningAuditEntryQueuing entry={entry}/>
                        }
                    />

                    <Column
                        key="timestamp"
                        title="Timestamp"
                        render={(_, entry) =>
                            <TimestampText
                                value={entry.mostRecentState.creation.time}
                                format="YYYY MMM DD, HH:mm:ss"
                            />
                        }
                    />

                    <Column
                        key="duration"
                        title="Duration"
                        render={(_, entry) =>
                            <Duration
                                displaySeconds={true}
                                seconds={Math.floor(entry.duration / 1000)}
                            />
                        }
                    />

                </Table>

                {/* TODO Pagination (more) */}

            </Space>
        </>
    )
}