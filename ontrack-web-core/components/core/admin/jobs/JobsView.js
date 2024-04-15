import Head from "next/head";
import {title} from "@components/common/Titles";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import MainPage from "@components/layouts/MainPage";
import StandardTable from "@components/common/table/StandardTable";
import {gql} from "graphql-request";
import {Space, Typography} from "antd";
import JobState from "@components/core/admin/jobs/JobState";
import JobActions from "@components/core/admin/jobs/JobActions";
import JobDetails from "@components/core/admin/jobs/JobDetails";
import TimestampText from "@components/common/TimestampText";
import TableColumnFilterDropdownInput from "@components/common/table/TableColumnFilterDropdownInput";
import {useState} from "react";
import TableColumnFilterDropdown from "@components/common/table/TableColumnFilterDropdown";
import SelectJobState from "@components/core/admin/jobs/SelectJobState";
import JobErrorIndicator from "@components/core/admin/jobs/JobErrorIndicator";
import SelectJobError from "@components/core/admin/jobs/SelectJobError";
import SelectJobTimeout from "@components/core/admin/jobs/SelectJobTimeout";
import JobTimeoutIndicator from "@components/core/admin/jobs/JobTimeoutIndicator";
import JobCategoriesContextProvider from "@components/core/admin/jobs/JobCategoriesContext";
import SelectJobCategory from "@components/core/admin/jobs/SelectJobCategory";
import SelectJobType from "@components/core/admin/jobs/SelectJobType";
import JobExecutionStatus from "@components/core/admin/jobs/JobExecutionStatus";
import {AutoRefreshButton, AutoRefreshContextProvider} from "@components/common/AutoRefresh";

export default function JobsView() {

    const query = gql`
        query SystemJobs(
            $offset: Int!,
            $size: Int!,
            $description: String,
            $state: JobState,
            $error: Boolean,
            $timeout: Boolean,
            $category: String,
            $type: String,
        ) {
            jobs(
                offset: $offset,
                size: $size,
                description: $description,
                state: $state,
                error: $error,
                timeout: $timeout,
                category: $category,
                type: $type,
            ) {
                pageInfo {
                    nextPage {
                        offset
                        size
                    }
                }
                pageItems {
                    key: id
                    id
                    jobKey: key {
                        id
                        type {
                            key
                            name
                            category {
                                key
                                name
                            }
                        }
                    }
                    description
                    state
                    canRun
                    canPause
                    canResume
                    canBeStopped
                    canBeDeleted
                    schedule {
                        periodText
                    }
                    runCount
                    lastRunDurationMs
                    lastErrorCount
                    lastTimeoutCount
                    lastRunDate
                    nextRunDate
                }
            }
        }
    `

    const [filter, setFilter] = useState({
        description: null,
        state: null,
        error: false,
        timeout: false,
        category: null,
        type: null,
    })

    const onFilterChange = (filters) => {
        setFilter({
            description: filters.description && filters.description[0],
            state: filters.state && filters.state[0],
            error: filters.error && filters.error[0],
            timeout: filters.timeout && filters.timeout[0],
            category: filters.category && (typeof filters.category === 'string' ? filters.category : filters.category[0]),
            type: filters.category ? filters.type && (typeof filters.type === 'string' ? filters.type : filters.type[0]) : null,
        })
    }

    const [reloadCount, setReloadCount] = useState(0)
    const refresh = () => {
        setReloadCount(previous => previous + 1)
    }

    return (
        <>
            <Head>
                {title("Background jobs")}
            </Head>
            <MainPage
                title="Background jobs"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="close" href={homeUri()}/>
                ]}
            >
                <Space direction="vertical">
                    <JobExecutionStatus/>
                    <JobCategoriesContextProvider>
                        <AutoRefreshContextProvider onRefresh={refresh}>
                            <StandardTable
                                query={query}
                                queryNode="jobs"
                                size={30}
                                reloadCount={reloadCount}
                                filter={filter}
                                onFilterChange={onFilterChange}
                                columns={[
                                    {
                                        key: 'id',
                                        title: 'ID',
                                        dataIndex: 'id',
                                    },
                                    {
                                        key: 'category',
                                        title: 'Category',
                                        render: (_, job) => job.jobKey.type.category.name,
                                        filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                            <TableColumnFilterDropdown
                                                confirm={confirm}
                                                clearFilters={clearFilters}
                                            >
                                                <SelectJobCategory
                                                    value={selectedKeys}
                                                    onChange={value => setSelectedKeys([value])}
                                                    style={{
                                                        width: '15em',
                                                    }}
                                                />
                                            </TableColumnFilterDropdown>
                                        ,
                                        filteredValue: filter.category,
                                    },
                                    {
                                        key: 'type',
                                        title: 'Type',
                                        render: (_, job) => job.jobKey.type.name,
                                        filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                            <TableColumnFilterDropdown
                                                confirm={confirm}
                                                clearFilters={clearFilters}
                                            >
                                                <SelectJobType
                                                    selectedCategory={filter.category}
                                                    value={selectedKeys}
                                                    onChange={value => setSelectedKeys([value])}
                                                    style={{
                                                        width: '15em',
                                                    }}
                                                />
                                            </TableColumnFilterDropdown>
                                        ,
                                        filteredValue: filter.type,
                                    },
                                    {
                                        key: 'description',
                                        title: 'Description',
                                        render: (_, job) => <Space direction="vertical">
                                            <Typography.Text
                                                code>{job.jobKey.type.category.key}/{job.jobKey.type.key}/{job.jobKey.id}</Typography.Text>
                                            <Typography.Text type="secondary">{job.description}</Typography.Text>
                                        </Space>,
                                        filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                            <TableColumnFilterDropdownInput
                                                confirm={confirm}
                                                clearFilters={clearFilters}
                                                selectedKeys={selectedKeys}
                                                setSelectedKeys={setSelectedKeys}
                                            />
                                        ,
                                        filteredValue: filter.description,
                                    },
                                    {
                                        key: 'state',
                                        title: 'State',
                                        render: (_, job) => <JobState value={job.state}/>,
                                        filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                            <TableColumnFilterDropdown
                                                confirm={confirm}
                                                clearFilters={clearFilters}
                                            >
                                                <SelectJobState
                                                    value={selectedKeys}
                                                    onChange={value => setSelectedKeys([value])}
                                                    style={{
                                                        width: '15em',
                                                    }}
                                                    allowClear={true}
                                                />
                                            </TableColumnFilterDropdown>
                                        ,
                                        filteredValue: filter.state,
                                    },
                                    {
                                        key: 'error',
                                        title: "Error",
                                        render: (_, job) => <JobErrorIndicator job={job}/>,
                                        filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                            <TableColumnFilterDropdown
                                                confirm={confirm}
                                                clearFilters={clearFilters}
                                            >
                                                <SelectJobError
                                                    value={selectedKeys}
                                                    onChange={value => setSelectedKeys([value])}
                                                    style={{
                                                        width: '15em',
                                                    }}
                                                />
                                            </TableColumnFilterDropdown>
                                        ,
                                        filteredValue: filter.error,
                                    },
                                    {
                                        key: 'timeout',
                                        title: "Timeout",
                                        render: (_, job) => <JobTimeoutIndicator job={job}/>,
                                        filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                                            <TableColumnFilterDropdown
                                                confirm={confirm}
                                                clearFilters={clearFilters}
                                            >
                                                <SelectJobTimeout
                                                    value={selectedKeys}
                                                    onChange={value => setSelectedKeys([value])}
                                                    style={{
                                                        width: '15em',
                                                    }}
                                                />
                                            </TableColumnFilterDropdown>
                                        ,
                                        filteredValue: filter.timeout,
                                    },
                                    {
                                        key: 'actions',
                                        title: 'Actions',
                                        render: (_, job) => <JobActions job={job} onDone={refresh}/>,
                                    },
                                    {
                                        key: 'schedule',
                                        title: 'Schedule',
                                        render: (_, job) => job.schedule.periodText,
                                    },
                                    {
                                        key: 'lastRunDate',
                                        title: 'Last run',
                                        render: (_, job) => <TimestampText value={job.lastRunDate}/>,
                                    },
                                ]}
                                expandable={{
                                    expandedRowRender: (job) => (
                                        <JobDetails job={job}/>
                                    )
                                }}
                                footerExtra={
                                    <AutoRefreshButton/>
                                }
                            />
                        </AutoRefreshContextProvider>
                    </JobCategoriesContextProvider>
                </Space>
            </MainPage>
        </>
    )
}