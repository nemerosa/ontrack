import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import LoadingContainer from "@components/common/LoadingContainer";
import {Button, DatePicker, Input, Popover, Space, Spin, Table, Typography} from "antd";
import BuildLink from "@components/builds/BuildLink";
import Decorations from "@components/framework/decorations/Decorations";
import TimestampText from "@components/common/TimestampText";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import {FaExchangeAlt, FaSearch} from "react-icons/fa";
import TableColumnFilterDropdown from "@components/common/TableColumnFilterDropdown";
import useRangeSelection from "@components/common/RangeSelection";
import RangeSelector from "@components/common/RangeSelector";
import {useRouter} from "next/router";
import {scmChangeLogUri} from "@components/common/Links";

const {Column} = Table

export default function PromotionLevelHistory({promotionLevel}) {

    const client = useGraphQLClient()
    const router = useRouter()

    const [pagination, setPagination] = useState({
        offset: 0,
        size: 5,
    })

    const [filter, setFilter] = useState({
        name: null,
        version: null,
        afterDate: null,
        beforeDate: null,
    })

    const onTableChange = (_, filters) => {
        setFilter({
            name: filters.build ? filters.build[0] : null,
            version: filters.build && filters.build.length > 0 ? filters.build[1] : null,
            afterDate: filters.creation ? filters.creation[0] : null,
            beforeDate: filters.creation && filters.creation.length > 0 ? filters.creation[1] : null,
        })
    }

    const [loading, setLoading] = useState(false)
    const [promotions, setPromotions] = useState([])
    const [pageInfo, setPageInfo] = useState()
    const [scmChangeLogEnabled, setScmChangeLogEnabled] = useState('')

    useEffect(() => {
        if (client && promotionLevel) {
            setLoading(true)
            client.request(
                gql`
                    query GetPromotionLevelHistory(
                        $id: Int!,
                        $offset: Int!,
                        $size: Int!,
                        $name: String,
                        $version: String,
                        $afterDate: LocalDateTime,
                        $beforeDate: LocalDateTime,
                    ) {
                        promotionLevel(id: $id) {
                            branch {
                                scmBranchInfo {
                                    changeLogs
                                }
                            }
                            promotionRuns: promotionRunsPaginated(
                                offset: $offset,
                                size: $size,
                                name: $name,
                                version: $version,
                                afterDate: $afterDate,
                                beforeDate: $beforeDate,
                            ) {
                                pageInfo {
                                    nextPage {
                                        offset
                                        size
                                    }
                                }
                                pageItems {
                                    id
                                    description
                                    annotatedDescription
                                    build {
                                        id
                                        name
                                        releaseProperty {
                                            value
                                        }
                                        decorations {
                                            decorationType
                                            error
                                            data
                                            feature {
                                                id
                                            }
                                        }
                                    }
                                    creation {
                                        user
                                        time
                                    }
                                }
                            }
                        }
                    }
                `,
                {
                    id: promotionLevel.id,
                    offset: pagination.offset,
                    size: pagination.size,
                    name: filter.name,
                    version: filter.version,
                    afterDate: filter.afterDate,
                    beforeDate: filter.beforeDate,
                }
            ).then(data => {
                setScmChangeLogEnabled(data.promotionLevel.branch.scmBranchInfo?.changeLogs)
                setPageInfo(data.promotionLevel.promotionRuns.pageInfo)
                if (pagination.offset > 0) {
                    setPromotions([...promotions, ...data.promotionLevel.promotionRuns.pageItems])
                } else {
                    setPromotions(data.promotionLevel.promotionRuns.pageItems)
                }
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, promotionLevel, pagination, filter]);

    const onLoadMore = () => {
        if (pageInfo.nextPage) {
            setPagination(pageInfo.nextPage)
        }
    }

    const rangeSelection = useRangeSelection()

    const onChangeLog = () => {
        if (scmChangeLogEnabled && rangeSelection.isComplete()) {
            const [runIdFrom, runIdTo] = rangeSelection.selection
            const runFrom = promotions.find(it => it.id === runIdFrom)
            const runTo = promotions.find(it => it.id === runIdTo)
            if (runFrom && runTo) {
                router.push(scmChangeLogUri(runFrom.build.id, runTo.build.id))
            }
        }
    }

    return (
        <>
            <LoadingContainer loading={loading && pagination.offset === 0} tip="Loading history...">
                <Table
                    dataSource={promotions}
                    pagination={false}
                    footer={() => (
                        <>
                            <Space>
                                <Popover
                                    content={
                                        (pageInfo && pageInfo.nextPage) ?
                                            "There are more promotion runs to be loaded" :
                                            "There are no more promotion runs to be loaded"
                                    }
                                >
                                    <Button
                                        onClick={onLoadMore}
                                        disabled={loading || !pageInfo || !pageInfo.nextPage}
                                    >
                                        <Space>
                                            {
                                                loading && pagination.offset && <Spin size="small"/>
                                            }
                                            {
                                                !loading && <FaSearch/>
                                            }
                                            <Typography.Text>Load more...</Typography.Text>
                                        </Space>
                                    </Button>
                                </Popover>
                                {
                                    scmChangeLogEnabled &&
                                    <Popover
                                        content="Change between two selected promotions."
                                    >
                                        <Button
                                            disabled={!rangeSelection || !rangeSelection.isComplete()}
                                            onClick={onChangeLog}
                                        >
                                            <Space>
                                                <FaExchangeAlt/>
                                                <Typography.Text>Change log</Typography.Text>
                                            </Space>
                                        </Button>
                                    </Popover>
                                }
                            </Space>
                        </>
                    )}
                    onChange={onTableChange}
                >
                    {
                        scmChangeLogEnabled &&
                        <Column
                            key="range"
                            width={32}
                            render={(_, run) =>
                                <RangeSelector
                                    id={run.id}
                                    title="Select this run as a boundary for a change log."
                                    rangeSelection={rangeSelection}
                                />
                            }
                        />
                    }
                    <Column
                        title="Build"
                        key="build"
                        render={(_, run) =>
                            <Space>
                                <BuildLink
                                    build={run.build}
                                />
                                <Decorations entity={run.build}/>
                            </Space>
                        }
                        filterDropdown={({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                            <TableColumnFilterDropdown
                                confirm={confirm}
                                clearFilters={clearFilters}
                            >
                                <Input
                                    placeholder="Build name"
                                    value={selectedKeys[0]}
                                    onChange={(e) => setSelectedKeys([e.target.value, selectedKeys[1]])}
                                    style={{width: 188, marginBottom: 8, display: 'block'}}
                                />
                                <Input
                                    placeholder="Build version"
                                    value={selectedKeys[1]}
                                    onChange={(e) => setSelectedKeys([selectedKeys[0], e.target.value])}
                                    style={{width: 188, marginBottom: 8, display: 'block'}}
                                />
                            </TableColumnFilterDropdown>
                        }
                        filteredValue={filter.name || filter.version ? [filter.name, filter.version] : null}
                    />
                    <Column
                        title="Creation"
                        key="creation"
                        render={(_, run) =>
                            <Space>
                                <TimestampText value={run.creation.time}/>
                                <Typography.Text disabled>
                                    ({run.creation.user})
                                </Typography.Text>
                            </Space>
                        }
                        filterDropdown={({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                            <TableColumnFilterDropdown
                                confirm={confirm}
                                clearFilters={clearFilters}
                            >
                                <DatePicker
                                    placeholder="After"
                                    value={selectedKeys[0]}
                                    onChange={date => setSelectedKeys([date, selectedKeys[1]])}
                                />
                                <DatePicker
                                    placeholder="Before"
                                    value={selectedKeys[1]}
                                    onChange={date => setSelectedKeys([selectedKeys[0], date])}
                                />
                            </TableColumnFilterDropdown>
                        }
                        filteredValue={filter.afterDate || filter.beforeDate ? [filter.afterDate, filter.beforeDate] : null}
                    />
                    <Column
                        title="Description"
                        key="description"
                        render={(_, run) =>
                            <AnnotatedDescription
                                entity={run}
                                disabled={false}
                            />
                        }
                    />
                </Table>
            </LoadingContainer>
        </>
    )
}