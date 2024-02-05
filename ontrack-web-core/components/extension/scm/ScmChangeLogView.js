import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import ChangeLogBuild from "@components/extension/scm/ChangeLogBuild";
import Head from "next/head";
import {buildKnownName, title} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import LoadingContainer from "@components/common/LoadingContainer";
import {downToBranchBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {branchUri} from "@components/common/Links";
import GridTable from "@components/grid/GridTable";
import GridTableContextProvider, {GridTableContext} from "@components/grid/GridTableContext";
import GitChangeLogCommits from "@components/extension/git/GitChangeLogCommits";
import ChangeLogIssues from "@components/extension/issues/ChangeLogIssues";
import ChangeLogLinks from "@components/extension/scm/ChangeLogLinks";
import {Skeleton, Typography} from "antd";

export default function ScmChangeLogView({from, to}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [changeLog, setChangeLog] = useState({
        buildFrom: {},
        buildTo: {},
    })

    const gqlBuildData = gql`
        fragment BuildData on Build {
            id
            name
            creation {
                time
            }
            branch {
                id
                name
                project {
                    id
                    name
                }
            }
            promotionRuns(lastPerLevel: true) {
                id
                creation {
                    time
                }
                promotionLevel {
                    id
                    name
                    description
                    image
                    _image
                }
            }
            releaseProperty {
                value
            }
        }
    `

    useEffect(() => {
        if (client && from && to) {
            setLoading(true)
            client.request(
                gql`
                    query ChangeLog($from: Int!, $to: Int!) {
                        scmChangeLog(from: $from, to: $to) {
                            buildFrom: from {
                                ...BuildData
                            }
                            buildTo: to {
                                ...BuildData
                            }
                            commits {
                                id
                                shortId
                                # TODO annotatedMessage
                                message
                                link
                                author
                                timestamp
#                                # TODO build {
#                                    id
#                                    name
#                                    creation {
#                                        time
#                                    }
#                                    releaseProperty {
#                                        value
#                                    }
#                                    promotionRuns(lastPerLevel: true) {
#                                        creation {
#                                            time
#                                        }
#                                        annotatedDescription
#                                        description
#                                        promotionLevel {
#                                            id
#                                            name
#                                            image
#                                            _image
#                                        }
#                                    }
#                                    usingQualified {
#                                        pageItems {
#                                            qualifier
#                                            build {
#                                                id
#                                                branch {
#                                                    project {
#                                                        name
#                                                    }
#                                                }
#                                                name
#                                                releaseProperty {
#                                                    value
#                                                }
#                                                creation {
#                                                    time
#                                                }
#                                            }
#                                        }
#                                    }
#                                }
                            }
                            issues {
                                issueServiceConfiguration {
                                    id
                                }
                                issues {
                                    displayKey
                                    summary
                                }
                            }
                        }
                    }
                    ${gqlBuildData}
                `,
                {from, to}
            ).then(data => {
                setChangeLog(data.scmChangeLog)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, from, to]);


    const defaultLayout = [
        {i: "from", x: 0, y: 0, w: 6, h: 5},
        {i: "to", x: 6, y: 0, w: 6, h: 5},
        {i: "links", x: 0, y: 5, w: 12, h: 7},
        {i: "commits", x: 0, y: 12, w: 12, h: 10},
        {i: "issues", x: 0, y: 22, w: 12, h: 10},
    ]

    const items = [
        {
            id: "from",
            content: <Skeleton loading={loading}>
                <ChangeLogBuild id="from" title={`From ${buildKnownName(changeLog.buildFrom)}`}
                                build={changeLog.buildFrom}/>
            </Skeleton>,
        },
        {
            id: "to",
            content: <Skeleton loading={loading}>
                <ChangeLogBuild id="to" title={`To ${buildKnownName(changeLog.buildTo)}`} build={changeLog.buildTo}/>
            </Skeleton>,
        },
        {
            id: "links",
            // TODO content: <ChangeLogLinks id="links" changeLogUuid={changeLogUuid}/>
            content: <Typography.Text>TODO Links</Typography.Text>
        },
        {
            id: "commits",
            content: <Skeleton loading={loading}>
                <GitChangeLogCommits id="commits" commits={changeLog.commits} diffLink={changeLog.diffLink}/>
            </Skeleton>,
        },
        {
            id: "issues",
            // TODO content: <ChangeLogIssues id="issues" changeLogUuid={changeLogUuid}/>
            content: <Typography.Text>TODO Issues</Typography.Text>
        },
    ]

    return (
        <>
            <Head>
                {title(`Change log | From ${buildKnownName(changeLog.buildFrom)} to ${buildKnownName(changeLog.buildTo)}`)}
            </Head>
            <MainPage
                title={
                    `Change log from ${buildKnownName(changeLog.buildFrom)} to ${buildKnownName(changeLog.buildTo)}`
                }
                breadcrumbs={changeLog.buildFrom.branch ? downToBranchBreadcrumbs(changeLog.buildFrom) : []}
                commands={[
                    <CloseCommand
                        key="close"
                        href={changeLog.buildFrom.branch ? branchUri(changeLog.buildFrom.branch) : ''}
                    />,
                ]}
            >
                <GridTableContextProvider isExpandable={false} isDraggable={false}>
                    <LoadingContainer loading={loading} tip="Loading change log">
                        <GridTable
                            rowHeight={30}
                            layout={defaultLayout}
                            items={items}
                            isResizable={false}
                            isDraggable={false}
                        />
                    </LoadingContainer>
                </GridTableContextProvider>
            </MainPage>
        </>
    )
}