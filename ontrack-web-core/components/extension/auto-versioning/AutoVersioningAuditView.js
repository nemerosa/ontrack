import {useContext, useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {Button, Input, Popover, Space, Spin, Table, Tooltip, Typography} from "antd";
import AutoVersioningAuditEntryTarget from "@components/extension/auto-versioning/AutoVersioningAuditEntryTarget";
import {FaSearch, FaSquare} from "react-icons/fa";
import AutoVersioningAuditEntryState from "@components/extension/auto-versioning/AutoVersioningAuditEntryState";
import AutoVersioningAuditEntryPR from "@components/extension/auto-versioning/AutoVersioningAuditEntryPR";
import AutoVersioningAuditEntryQueuing from "@components/extension/auto-versioning/AutoVersioningAuditEntryQueuing";
import TimestampText from "@components/common/TimestampText";
import Duration from "@components/common/Duration";
import Link from "next/link";
import TableColumnFilterDropdown from "@components/common/table/TableColumnFilterDropdown";
import TableColumnFilterDropdownInput from "@components/common/table/TableColumnFilterDropdownInput";
import SelectAutoVersioningAuditState from "@components/extension/auto-versioning/SelectAutoVersioningAuditState";
import SelectAutoVersioningAuditRunningState
    from "@components/extension/auto-versioning/SelectAutoVersioningAuditRunningState";
import {AutoVersioningAuditContext} from "@components/extension/auto-versioning/AutoVersioningAuditContext";
import ProjectLinkByName from "@components/projects/ProjectLinkByName";
import {AutoRefreshButton, AutoRefreshContextProvider} from "@components/common/AutoRefresh";
import AutoVersioningApproval from "@components/extension/auto-versioning/AutoVersioningApproval";
import {autoVersioningAuditEntryUri} from "@components/common/Links";

const {Column} = Table

export default function AutoVersioningAuditView() {

    const context = useContext(AutoVersioningAuditContext)

    const client = useGraphQLClient()

    const [filterReady, setFilterReady] = useState(false)
    const [loading, setLoading] = useState(true)
    const [entries, setEntries] = useState([])

    const [pagination, setPagination] = useState({
        offset: 0,
        size: 20,
    })

    const [pageInfo, setPageInfo] = useState({})

    const [filter, setFilter] = useState({
        targetProject: null,
        targetBranch: null,
        sourceProject: null,
        version: null,
        state: null,
        running: null,
        routing: null,
        queue: null,
    })

    useEffect(() => {
        if (context) {
            const {sourceProject, targetProject, targetBranch} = context
            if (sourceProject) {
                setFilter(filter => ({...filter, sourceProject: sourceProject.name}))
            } else if (targetProject) {
                setFilter(filter => ({...filter, targetProject: targetProject.name}))
            } else if (targetBranch) {
                setFilter(filter => ({
                    ...filter,
                    targetProject: targetBranch.project.name,
                    targetBranch: targetBranch.name
                }))
            }
            setFilterReady(true)
        }
    }, [context]);

    const onTableChange = (_, filters) => {
        setFilter({
            targetProject: filters.target ? filters.target[0] : null,
            targetBranch: filters.target && filters.target.length > 0 ? filters.target[1] : null,
            sourceProject: filters.source && filters.source[0],
            version: filters.version && filters.version[0],
            state: filters.state && filters.state[0],
            running: filters.running && filters.running[0],
            routing: filters.queuing && filters.queuing[0],
            queue: filters.queuing && filters.queuing.length > 0 && filters.queuing[1],
        })
    }

    const onRefresh = () => {
        if (client && filterReady) {
            setLoading(true)
            client.request(
                gql`
                    query AutoVersioningAudit(
                        $offset: Int!,
                        $size: Int!,
                        $targetProject: String,
                        $targetBranch: String,
                        $sourceProject: String,
                        $version: String,
                        $state: String,
                        $running: Boolean,
                        $routing: String,
                        $queue: String,
                    ) {
                        autoVersioningAuditEntries(
                            offset: $offset,
                            size: $size,
                            filter: {
                                project: $targetProject,
                                branch: $targetBranch,
                                source: $sourceProject,
                                version: $version,
                                state: $state,
                                running: $running,
                                routing: $routing,
                                queue: $queue,
                            }
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
                                    sourcePromotion
                                    branch {
                                        id
                                        name
                                        project {
                                            id
                                            name
                                        }
                                    }
                                    qualifier
                                    repositoryHtmlURL
                                    targetPath
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
                    targetProject: filter.targetProject,
                    targetBranch: filter.targetBranch,
                    sourceProject: filter.sourceProject,
                    version: filter.version,
                    state: filter.state,
                    running: filter.running,
                    routing: filter.routing,
                    queue: filter.queue,
                }
            )
            .then(data => {
                setPageInfo(data.autoVersioningAuditEntries.pageInfo)
                if (pagination.offset > 0) {
                    setEntries((entries) => [...entries, ...data.autoVersioningAuditEntries.pageItems])
                } else {
                    setEntries(data.autoVersioningAuditEntries.pageItems)
                }
            })
            .finally(() => {
                setLoading(false)
            })
        }
    }

    useEffect(onRefresh, [client, pagination, filter])

    const onLoadMore = () => {
        if (pageInfo.nextPage) {
            setPagination(pageInfo.nextPage)
        }
    }

    return (
        <>
            <Space className="ot-line" direction="vertical">
                <AutoRefreshContextProvider onRefresh={onRefresh}>
                    <Table
                        dataSource={entries}
                        loading={loading}
                        pagination={false}
                        size="small"
                        onChange={onTableChange}
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
                                    <AutoRefreshButton/>
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
                                        href={autoVersioningAuditEntryUri(entry.order.uuid)}>{entry.order.uuid}</Link>
                                </Tooltip>
                            }
                        />

                        {
                            <Column
                                key="target"
                                title="Target"
                                render={(_, entry) =>
                                    <AutoVersioningAuditEntryTarget entry={entry}/>
                                }
                                filterDropdown={({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                    <TableColumnFilterDropdown
                                        confirm={confirm}
                                        clearFilters={clearFilters}
                                    >
                                        <Input
                                            placeholder="Target project"
                                            value={selectedKeys[0]}
                                            onChange={(e) => setSelectedKeys([e.target.value, selectedKeys[1]])}
                                            style={{width: 188, marginBottom: 8, display: 'block'}}
                                        />
                                        <Input
                                            placeholder="Target branch"
                                            value={selectedKeys[1]}
                                            onChange={(e) => setSelectedKeys([selectedKeys[0], e.target.value])}
                                            style={{width: 188, marginBottom: 8, display: 'block'}}
                                        />
                                    </TableColumnFilterDropdown>
                                }
                                filteredValue={filter.targetProject || filter.targetBranch ? [filter.targetProject, filter.targetBranch] : null}
                            />
                        }

                        {!context.sourceProject &&
                            <Column
                                key="source"
                                title="Source project"
                                render={(_, entry) =>
                                    <ProjectLinkByName name={entry.order.sourceProject}/>
                                }
                                filterDropdown={({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                    <TableColumnFilterDropdownInput
                                        confirm={confirm}
                                        clearFilters={clearFilters}
                                        placeholder="Source project"
                                        selectedKeys={selectedKeys}
                                        setSelectedKeys={setSelectedKeys}
                                    />
                                }
                                filteredValue={filter.sourceProject}
                            />
                        }

                        <Column
                            key="promotion"
                            title="Promotion"
                            render={(_, entry) =>
                                <Typography.Text>{entry.order.sourcePromotion}</Typography.Text>
                            }
                        />

                        <Column
                            key="qualifier"
                            title="Qualifier"
                            render={(_, entry) =>
                                <Typography.Text>{entry.order.qualifier}</Typography.Text>
                            }
                        />

                        <Column
                            key="version"
                            title="Version"
                            render={(_, entry) =>
                                <Typography.Text>{entry.order.targetVersion}</Typography.Text>
                            }
                            filterDropdown={({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                <TableColumnFilterDropdownInput
                                    confirm={confirm}
                                    clearFilters={clearFilters}
                                    placeholder="Version"
                                    selectedKeys={selectedKeys}
                                    setSelectedKeys={setSelectedKeys}
                                />
                            }
                            filteredValue={filter.version}
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
                                    <AutoVersioningApproval
                                        autoApproval={entry.order.autoApproval}
                                        autoApprovalMode={entry.order.autoApprovalMode}
                                    />
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
                            filterDropdown={({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                <TableColumnFilterDropdown
                                    confirm={confirm}
                                    clearFilters={clearFilters}
                                >
                                    <SelectAutoVersioningAuditRunningState
                                        value={selectedKeys}
                                        onChange={value => setSelectedKeys([value])}
                                    />
                                </TableColumnFilterDropdown>
                            }
                            filteredValue={filter.running}
                        />

                        <Column
                            key="state"
                            title="State"
                            render={(_, entry) =>
                                <AutoVersioningAuditEntryState status={entry.mostRecentState}/>
                            }
                            filterDropdown={({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                <TableColumnFilterDropdown
                                    confirm={confirm}
                                    clearFilters={clearFilters}
                                >
                                    <SelectAutoVersioningAuditState
                                        value={selectedKeys}
                                        onChange={value => setSelectedKeys([value])}
                                    />
                                </TableColumnFilterDropdown>
                            }
                            filteredValue={filter.state}
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
                            filterDropdown={({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                <TableColumnFilterDropdown
                                    confirm={confirm}
                                    clearFilters={clearFilters}
                                >
                                    <Input
                                        placeholder="Routing"
                                        value={selectedKeys[0]}
                                        onChange={(e) => setSelectedKeys([e.target.value, selectedKeys[1]])}
                                        style={{width: 188, marginBottom: 8, display: 'block'}}
                                    />
                                    <Input
                                        placeholder="Queue"
                                        value={selectedKeys[1]}
                                        onChange={(e) => setSelectedKeys([selectedKeys[0], e.target.value])}
                                        style={{width: 188, marginBottom: 8, display: 'block'}}
                                    />
                                </TableColumnFilterDropdown>
                            }
                            filteredValue={filter.routing || filter.queue ? [filter.routing, filter.queue] : null}
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
                </AutoRefreshContextProvider>
            </Space>
        </>
    )
}