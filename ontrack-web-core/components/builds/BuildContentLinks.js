import GridCell from "@components/grid/GridCell";
import GridCellCommand from "@components/grid/GridCellCommand";
import {FaProjectDiagram, FaSearch} from "react-icons/fa";
import {buildLinksUri} from "@components/common/Links";
import {Button, Popover, Space, Table, Typography} from "antd";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import ProjectLink from "@components/projects/ProjectLink";
import BuildLink from "@components/builds/BuildLink";

const {Column} = Table

export default function BuildContentLinks({build, id, title, fieldName}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [links, setLinks] = useState([])


    const [pagination, setPagination] = useState({
        offset: 0,
        size: 10,
    })

    const [pageInfo, setPageInfo] = useState({
        nextPage: null,
    })

    useEffect(() => {
        if (client && build) {
            setLoading(true)
            client.request(
                `
                    query GetBuildLinks(
                        $id: Int!,
                        $offset: Int!,
                        $size: Int!,
                    ) {
                        build(id: $id) {
                            ${fieldName}(offset: $offset, size: $size) {
                                pageInfo {
                                    nextPage {
                                        offset
                                        size
                                    }
                                }
                                pageItems {
                                    qualifier
                                    build {
                                        id
                                        name
                                        releaseProperty {
                                            value
                                        }
                                        branch {
                                            id
                                            name
                                            project {
                                                id
                                                name
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                `,
                {
                    id: Number(build.id),
                    offset: pagination.offset,
                    size: pagination.size,
                }
            ).then(data => {
                setPageInfo(data.build[fieldName].pageInfo)
                if (pagination.offset > 0) {
                    setLinks((links) => [...links, ...data.build[fieldName].pageItems])
                } else {
                    setLinks(data.build[fieldName].pageItems)
                }
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, build, pagination, fieldName]);

    const onLoadMore = () => {
        if (pageInfo.nextPage) {
            setPagination(pageInfo.nextPage)
        }
    }

    return (
        <>
            <GridCell id={id}
                      title={title}
                      extra={
                          <>
                              <GridCellCommand
                                  icon={<FaProjectDiagram/>}
                                  title="Graph of build links"
                                  href={buildLinksUri(build)}
                              />
                          </>
                      }
            >
                <Table
                    loading={loading}
                    dataSource={links}
                    pagination={false}
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
                        key="project"
                        title="Project"
                        render={(_, link) => <ProjectLink project={link.build.branch.project}/>}
                    />
                    <Column
                        key="qualifier"
                        title="Qualifier"
                        dataIndex="qualifier"
                    />
                    <Column
                        key="build"
                        title="Build"
                        render={(_, link) => <BuildLink build={link.build}/>}
                    />
                </Table>
            </GridCell>
        </>
    )
}