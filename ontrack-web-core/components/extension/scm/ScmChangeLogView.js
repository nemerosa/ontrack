import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import ChangeLogBuild from "@components/extension/scm/ChangeLogBuild";
import Head from "next/head";
import {buildKnownName, title} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import LoadingContainer from "@components/common/LoadingContainer";
import {downToBranchBreadcrumbs, homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {branchUri, homeUri} from "@components/common/Links";
import GridTable from "@components/grid/GridTable";
import GridTableContextProvider from "@components/grid/GridTableContext";
import GitChangeLogCommits from "@components/extension/git/GitChangeLogCommits";
import ChangeLogIssues from "@components/extension/issues/ChangeLogIssues";
import ChangeLogLinks from "@components/extension/scm/ChangeLogLinks";
import {Empty, Skeleton, Typography} from "antd";

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
                            diffLink
                            linkChanges {
                                project {
                                    id
                                    name
                                }
                                qualifier
                                from {
                                    branch {
                                        scmBranchInfo {
                                            changeLogs
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
                            commits {
                                commit {
                                    id
                                    shortId
                                    message
                                    link
                                    author
                                    timestamp
                                }
                                annotatedMessage
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
                            issues {
                                issueServiceConfiguration {
                                    serviceId
                                }
                                issues {
                                    displayKey
                                    summary
                                    url
                                    status {
                                        name
                                    }
                                    updateTime
                                    rawIssue
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

    const [items, setItems] = useState([])

    useEffect(() => {
        if (changeLog) {
            setItems(
                [
                    {
                        id: "from",
                        content: <Skeleton loading={loading}>
                            {
                                changeLog.buildFrom.creation &&
                                <ChangeLogBuild id="from" title={`From ${buildKnownName(changeLog.buildFrom)}`}
                                                build={changeLog.buildFrom}/>
                            }
                        </Skeleton>,
                    },
                    {
                        id: "to",
                        content: <Skeleton loading={loading}>
                            {
                                changeLog.buildTo.creation &&
                                <ChangeLogBuild id="to" title={`To ${buildKnownName(changeLog.buildTo)}`}
                                                build={changeLog.buildTo}/>
                            }
                        </Skeleton>,
                    },
                    {
                        id: "links",
                        content: <Skeleton loading={loading}>
                            <ChangeLogLinks id="links" linkChanges={changeLog.linkChanges}/>
                        </Skeleton>,
                    },
                    {
                        id: "commits",
                        content: <Skeleton loading={loading}>
                            <GitChangeLogCommits id="commits" commits={changeLog.commits}
                                                 diffLink={changeLog.diffLink}/>
                        </Skeleton>,
                    },
                    {
                        id: "issues",
                        content: <Skeleton loading={loading}>
                            <ChangeLogIssues id="issues" from={from} to={to} issues={changeLog.issues}/>
                        </Skeleton>,
                    },
                ]
            )
        }
    }, [changeLog]);

    return (
        <>
            <Head>
                {
                    changeLog ?
                        title(`Change log | From ${buildKnownName(changeLog.buildFrom)} to ${buildKnownName(changeLog.buildTo)}`) :
                        "Change log"
                }
            </Head>
            <MainPage
                title={
                    changeLog ?
                        `Change log from ${buildKnownName(changeLog.buildFrom)} to ${buildKnownName(changeLog.buildTo)}` :
                        "No change log"
                }
                breadcrumbs={
                    changeLog && changeLog.buildFrom.branch ? downToBranchBreadcrumbs(changeLog.buildFrom) : homeBreadcrumbs()
                }
                commands={[
                    <CloseCommand
                        key="close"
                        href={
                            changeLog && changeLog.buildFrom.branch ? branchUri(changeLog.buildFrom.branch) : homeUri()
                        }
                    />,
                ]}
            >
                {
                    changeLog && items &&
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
                }
                {
                    !changeLog && <Empty
                        description={
                            <Typography.Text>
                                No change log could be computed for these builds.

                                It&apos;s likely you have been redirected to a wrong page.
                            </Typography.Text>
                        }
                    />
                }
            </MainPage>
        </>
    )
}