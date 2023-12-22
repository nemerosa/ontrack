import GridCell from "@components/grid/GridCell";
import {Col, Row} from "antd";
import GitChangeLogCommit from "@components/extension/git/GitChangeLogCommit";
import GitChangeLogCommitsPlot from "@components/extension/git/GitChangeLogCommitsPlot";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import Link from "next/link";

export default function GitChangeLogCommits({id, changeLogUuid, diffLink}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [changeLog, setChangeLog] = useState({})

    useEffect(() => {
        if (client && changeLogUuid) {
            setLoading(true)
            client.request(
                gql`
                    query ChangeLogCommits($uuid: String!) {
                        gitChangeLogByUUID(uuid: $uuid) {
                            commitsPlot
                            commits {
                                id
                                shortId
                                annotatedMessage
                                link
                                author
                                timestamp
                                build {
                                    id
                                    name
                                    creation {
                                        time
                                    }
                                    releaseProperty {
                                        value
                                    }
                                    promotionRuns(lastPerLevel: true) {
                                        creation {
                                            time
                                        }
                                        annotatedDescription
                                        description
                                        promotionLevel {
                                            id
                                            name
                                            image
                                            _image
                                        }
                                    }
                                    usingQualified {
                                        pageItems {
                                            qualifier
                                            build {
                                                id
                                                branch {
                                                    project {
                                                        name
                                                    }
                                                }
                                                name
                                                releaseProperty {
                                                    value
                                                }
                                                creation {
                                                    time
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                `,
                {uuid: changeLogUuid}
            ).then(data => {
                setChangeLog(data.gitChangeLogByUUID)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, changeLogUuid]);

    const [graphWidth, setGraphWidth] = useState(32)

    const onComputedWidth = (width) => {
        setGraphWidth(width)
    }

    return (
        <>
            <GridCell
                id={id}
                title={
                    <>
                        Commits (<Link href={diffLink}>diff</Link>)
                    </>
                }
                loading={loading}
            >
                <Row>
                    <Col style={{width: graphWidth}}>
                        {
                            changeLog.commitsPlot &&
                            <GitChangeLogCommitsPlot
                                plot={changeLog.commitsPlot}
                                onComputedWidth={onComputedWidth}
                            />
                        }
                    </Col>
                    <Col>
                        {
                            changeLog.commits &&
                            changeLog.commits.map(commit =>
                                <GitChangeLogCommit
                                    key={commit.id}
                                    commit={commit}
                                />
                            )
                        }
                    </Col>
                </Row>
            </GridCell>
        </>
    )
}
