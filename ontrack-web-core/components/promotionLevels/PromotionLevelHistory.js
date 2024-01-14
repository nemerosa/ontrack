import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import LoadingContainer from "@components/common/LoadingContainer";
import {Button, Popover, Space, Table, Typography} from "antd";
import BuildLink from "@components/builds/BuildLink";
import Decorations from "@components/framework/decorations/Decorations";
import TimestampText from "@components/common/TimestampText";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import {FaSearch} from "react-icons/fa";

const {Column} = Table

// TODO Filter on build name
// TODO Filter on build version
// TODO Filter on after date
// TODO Filter on before date

export default function PromotionLevelHistory({promotionLevel}) {

    const client = useGraphQLClient()

    const [pagination, setPagination] = useState({
        offset: 0,
        size: 5,
    })

    const [loading, setLoading] = useState(false)
    const [promotions, setPromotions] = useState([])
    const [pageInfo, setPageInfo] = useState()

    useEffect(() => {
        if (client && promotionLevel) {
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
                                    description
                                    annotatedDescription
                                    build {
                                        id
                                        name
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
                    name: undefined,
                    version: undefined,
                    afterDate: undefined,
                    beforeDate: undefined,
                }
            ).then(data => {
                setPageInfo(data.promotionLevel.promotionRuns.pageInfo)
                if (pagination.offset > 0) {
                    setPromotions([...promotions, ...data.promotionLevel.promotionRuns.pageItems])
                } else {
                    setPromotions(data.promotionLevel.promotionRuns.pageItems)
                }
            })
        }
    }, [client, promotionLevel, pagination]);

    const onLoadMore = () => {
        if (pageInfo.nextPage) {
            setPagination(pageInfo.nextPage)
        }
    }

    return (
        <>
            <LoadingContainer loading={loading} tip="Loading history...">
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