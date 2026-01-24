import {useState} from "react";
import {gql} from "graphql-request";
import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {downToProjectBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {projectUri} from "@components/common/Links";
import LoadingContainer from "@components/common/LoadingContainer";
import SCMIssueInfo from "@components/extension/scm/SCMIssueInfo";
import {Empty} from "antd";
import {useQuery} from "@components/services/GraphQL";

export default function SCMIssueInfoView({projectName, issueKey}) {

    const [issueDisplayKey, setIssueDisplayKey] = useState(issueKey)

    const {loading, data: project} = useQuery(
        gql`
            query ScmIssueInfo(
                $projectName: String!,
                $issueKey: String!,
            ) {
                projects(name: $projectName) {
                    id
                    name
                    scmIssueInfo(issueKey: $issueKey) {
                        issueServiceConfigurationRepresentation {
                            id
                            serviceId
                            name
                        }
                        issue {
                            displayKey
                            url
                            summary
                            rawIssue
                        }
                        scmCommitInfo {
                            scmDecoratedCommit {
                                commit {
                                    id
                                    shortId
                                    author
                                    timestamp
                                    message
                                    link
                                }
                                annotatedMessage
                            }
                            branchInfos {
                                type
                                branchInfoList {
                                    branch {
                                        id
                                        name
                                        displayName
                                        disabled
                                        project {
                                            id
                                            name
                                        }
                                    }
                                    firstBuild {
                                        id
                                        name
                                        displayName
                                        creation {
                                            time
                                        }
                                    }
                                    promotions {
                                        id
                                        promotionLevel {
                                            id
                                            name
                                            image
                                        }
                                        build {
                                            id
                                            name
                                            displayName
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
        {
            initialData: {
                id: 0,
                name: projectName,
            },
            variables: {
                projectName,
                issueKey,
            },
            dataFn: data => {
                const project = data.projects ? data.projects[0] : {id: 0, name: projectName}
                if (project && project.scmIssueInfo) {
                    setIssueDisplayKey(project.scmIssueInfo.issue.displayKey)
                }
                return project
            },
            deps: [projectName, issueKey],
        }
    )

    return (
        <>
            <Head>
                {pageTitle(`${projectName} issue ${issueDisplayKey}`)}
            </Head>
            <MainPage
                title={`Issue ${issueDisplayKey}`}
                breadcrumbs={project ? downToProjectBreadcrumbs({project}) : []}
                commands={[
                    <CloseCommand key="close" href={project ? projectUri(project) : ""}/>,
                ]}
            >
                <LoadingContainer loading={loading}>
                    {
                        project && project.scmIssueInfo && <SCMIssueInfo scmIssueInfo={project.scmIssueInfo}/>
                    }
                    {
                        (!project || !project.scmIssueInfo) && <Empty/>
                    }
                </LoadingContainer>
            </MainPage>
        </>
    )
}