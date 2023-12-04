import React, {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {Button, Popover, Space, Table, Typography} from "antd";
import ValidationStamp from "@components/validationStamps/ValidationStamp";
import ValidationRunLink from "@components/validationRuns/ValidationRunLink";
import ValidationRunStatus from "@components/validationRuns/ValidationRunStatus";
import Duration from "@components/common/Duration";
import Timestamp from "@components/common/Timestamp";
import {FaEraser, FaGavel, FaInfoCircle} from "react-icons/fa";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import ValidationRunData from "@components/framework/validation-run-data/ValidationRunData";
import ValidationRunSortingMode from "@components/validationRuns/ValidationRunSortingMode";
import BuildValidateAction from "@components/builds/BuildValidateAction";
import {isAuthorized} from "@components/common/authorizations";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import GridCell from "@components/grid/GridCell";

export default function BuildContentValidations({build}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [validationRuns, setValidationRuns] = useState([])

    const [pageRequest, setPageRequest] = useState({
        offset: 0,
        size: 5,
    })

    // Filters
    const [filteredInfo, setFilteredInfo] = useState({})

    const resetFilters = () => {
        setFilteredInfo({})
    }

    // Sorting
    const [sortingMode, setSortingMode] = useState("ID")
    const onSortingModeChanged = (value) => {
        setPageRequest({
            offset: 0,
            size: 5,
        })
        setSortingMode(value)
    }

    const onTableChange = (pagination, filters, sorter, extra) => {
        if (extra.action === 'paginate') {
            setPageRequest({
                offset: (pagination.current - 1) * pagination.pageSize,
                size: pagination.pageSize,
            })
        } else if (extra.action === 'filter') {
            setPageRequest({offset: 0, size: 5}) // Resetting the pagination
            setFilteredInfo(filters)
        }
    }

    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 5,
        total: 0,
    })

    const sortingByText = (a, b) => {
        if (a.text < b.text) {
            return -1
        } else if (a.text > b.text) {
            return 1
        } else {
            return 0
        }
    }

    const [reloadCount, setReloadCount] = useState(0)

    const reload = () => {
        setReloadCount(reloadCount + 1)
    }

    const [statuses, setStatuses] = useState([])
    const [validationStamps, setValidationStamps] = useState([])
    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query ValidationRunFilters($branchId: Int!) {
                        validationRunStatusIDList {
                            value: id
                            text: name
                        }
                        branches(id: $branchId) {
                            validationStamps {
                                value: name
                                text: name
                            }
                        }
                    }
                `, {branchId: build.branch.id}
            ).then(data => {
                setStatuses(data.validationRunStatusIDList.sort(sortingByText))
                setValidationStamps(data.branches[0].validationStamps.sort(sortingByText))
            })
        }
    }, [client]);

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query BuildValidations(
                        $buildId: Int!,
                        $offset: Int!,
                        $size: Int!,
                        $sortingMode: ValidationRunSortingMode!,
                        $statuses: [String],
                        $validationStamp: String,
                    ) {
                        build(id: $buildId) {
                            validationRunsPaginated(
                                sortingMode: $sortingMode,
                                offset: $offset,
                                size: $size,
                                statuses: $statuses,
                                validationStamp: $validationStamp
                            ) {
                                pageInfo {
                                    pageIndex
                                    totalSize
                                    currentSize
                                    previousPage {
                                        offset
                                        size
                                    }
                                    nextPage {
                                        offset
                                        size
                                    }
                                }
                                pageItems {
                                    id
                                    key: id
                                    runOrder
                                    runInfo {
                                        runTime
                                    }
                                    lastStatus {
                                        creation {
                                            time
                                            user
                                        }
                                        description
                                        annotatedDescription
                                        statusID {
                                            id
                                            name
                                        }
                                    }
                                    validationStamp {
                                        id
                                        name
                                        image
                                        description
                                        annotatedDescription
                                    }
                                    data {
                                        descriptor {
                                            feature {
                                                id
                                            }
                                            id
                                        }
                                        data
                                    }
                                }
                            }
                        }
                    }
                `, {
                    buildId: build.id,
                    offset: pageRequest.offset,
                    size: pageRequest.size,
                    statuses: filteredInfo.status,
                    validationStamp: filteredInfo.validation ? filteredInfo.validation[0] : null,
                    sortingMode: sortingMode,
                }
            ).then(data => {
                setValidationRuns(data.build.validationRunsPaginated.pageItems)
                const pageInfo = data.build.validationRunsPaginated.pageInfo;
                setPagination({
                    ...pagination,
                    current: pageInfo.pageIndex + 1,
                    pageSize: pageRequest.size,
                    total: pageInfo.totalSize,
                })
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, build, pageRequest, filteredInfo, sortingMode, reloadCount]);

    // Definition of the columns

    const columns = [
        {
            title: "Validation",
            key: 'validation',
            render: (_, run) => <ValidationStamp validationStamp={run.validationStamp} tooltipPlacement="rightBottom"/>,
            filters: validationStamps,
            filterSearch: true,
            filterMultiple: false,
            filteredValue: filteredInfo.validation || null,
        },
        {
            title: "Run",
            key: 'run',
            render: (_, run) => <ValidationRunLink run={run}/>
        },
        {
            title: "Status",
            key: 'status',
            render: (_, run) => <ValidationRunStatus status={run.lastStatus}/>,
            filters: statuses,
            filterSearch: true,
            filteredValue: filteredInfo.status || null,
        },
        {
            title: "Description",
            key: 'description',
            render: (_, run) => <AnnotatedDescription entity={run.lastStatus}/>,
        },
        {
            title: "Creation",
            key: 'creation',
            render: (_, run) => <Popover
                content={
                    <Space direction="vertical">
                        <Typography.Text>Created by {run.lastStatus.creation.user}</Typography.Text>
                        <AnnotatedDescription entity={run.lastStatus}/>
                    </Space>
                }
            >
                <Space>
                    <FaInfoCircle/>
                    <Timestamp value={run.lastStatus.creation.time} fontSize="100%"></Timestamp>
                </Space>
            </Popover>
        },
        {
            title: "Duration",
            key: 'runtime',
            render: (_, run) => <Duration seconds={run.runInfo?.runTime}/>
        },
        {
            title: "Data",
            key: 'data',
            render: (_, run) => <ValidationRunData data={run.data}/>
        }
    ]

    return (
        <>
            <GridCell title="Validations"
                      loading={loading}
                      padding={false}
                      extra={
                          <>
                              <Space>
                                  {/* Validates the current build */}
                                  {
                                      isAuthorized(build, 'build', 'validate') &&
                                      <BuildValidateAction
                                          build={build}
                                          onValidation={reload}
                                      >
                                          <Button>
                                              <Space>
                                                  <FaGavel/>
                                                  <Typography.Text>Validate</Typography.Text>
                                              </Space>
                                          </Button>
                                      </BuildValidateAction>
                                  }
                                  {/* Reset filter */}
                                  <Button onClick={resetFilters}>
                                      <Space>
                                          <FaEraser/>
                                          <Typography.Text>Reset filters</Typography.Text>
                                      </Space>
                                  </Button>
                                  {/* Sorting mode */}
                                  <Typography.Text>Sort:</Typography.Text>
                                  <ValidationRunSortingMode value={sortingMode} onChange={onSortingModeChanged}/>
                              </Space>
                          </>
                      }
            >
                <Table
                    dataSource={validationRuns}
                    columns={columns}
                    pagination={pagination}
                    onChange={onTableChange}
                />
            </GridCell>
        </>
    )
}