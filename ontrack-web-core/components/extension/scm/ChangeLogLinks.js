import GridCell from "@components/grid/GridCell";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {Table} from "antd";
import ProjectLink from "@components/projects/ProjectLink";
import BuildLink from "@components/builds/BuildLink";
import ChangeLogSignLink from "@components/extension/scm/ChangeLogSignLink";

const {Column} = Table

export default function ChangeLogLinks({id, changeLogUuid}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [changes, setChanges] = useState([])

    useEffect(() => {
        if (client && changeLogUuid) {
            setLoading(true)
            client.request(
                gql`
                    query ChangeLogLinks($uuid: String!) {
                        gitChangeLogByUUID(uuid: $uuid) {
                            linkChanges {
                                project {
                                    id
                                    name
                                }
                                qualifier
                                from {
                                    branch {
                                        scmBranchInfo {
                                            type
                                        }
                                    }
                                    id
                                    name
                                    releaseProperty {
                                        value
                                    }
                                }
                                to {
                                    id
                                    name
                                    releaseProperty {
                                        value
                                    }
                                }
                            }
                        }
                    }
                `,
                {uuid: changeLogUuid}
            ).then(data => {
                setChanges(data.gitChangeLogByUUID.linkChanges)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, changeLogUuid]);

    return (
        <>
            <GridCell id={id} title="Links" padding={0} loading={loading}>
                <Table
                    dataSource={changes}
                    size="small"
                >
                    <Column
                        key="project"
                        title="Project"
                        render={(_, change) =>
                            <ProjectLink project={change.project}/>
                        }
                    />
                    <Column
                        key="qualifier"
                        title="Qualifier"
                        dataIndex="qualifier"
                    />
                    <Column
                        key="from"
                        title="Before"
                        render={(_, change) =>
                            change.from ? <BuildLink build={change.from}/> : "-"
                        }
                    />
                    <Column
                        key="to"
                        title="After"
                        render={(_, change) =>
                            change.to ? <BuildLink build={change.to}/> : "-"
                        }
                    />
                    <Column
                        key="change"
                        title=""
                        render={(_, change) =>
                            <ChangeLogSignLink
                                from={change.from}
                                to={change.to}
                            />
                        }
                    />
                </Table>
            </GridCell>
        </>
    )
}