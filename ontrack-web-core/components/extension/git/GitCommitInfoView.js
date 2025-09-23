import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {downToProjectBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {projectUri} from "@components/common/Links";
import LoadingContainer from "@components/common/LoadingContainer";
import {Empty} from "antd";
import SCMCommitInfo from "@components/extension/scm/SCMCommitInfo";

export default function GitCommitInfoView({projectId, commit}) {
    const {loading, data} = useQuery(
        gql`
            query GitCommitInfo(
                $projectId: Int!,
                $commit: String!,
            ) {
                project(id: $projectId) {
                    id
                    name
                    scmCommitInfo(commit: $commit) {
                        scmCommit {
                            id
                            shortId
                            author
                            timestamp
                            message
                            link
                        }
                    }
                }
            }
        `,
        {
            initialData: {
                project: {
                    id: projectId,
                    name: ""
                }
            },
            variables: {
                projectId: Number(projectId),
                commit,
            },
            deps: [projectId, commit],
        }
    )

    return (
        <>
            <Head>
                {pageTitle(`${data.project.name} commit ${commit}`)}
            </Head>
            <MainPage
                title={`Commit ${commit}`}
                breadcrumbs={downToProjectBreadcrumbs(data)}
                commands={[
                    <CloseCommand key="close" href={projectUri(data.project)}/>,
                ]}
            >
                <LoadingContainer loading={loading}>
                    {
                        data.project.scmCommitInfo && <SCMCommitInfo scmCommitInfo={data.project.scmCommitInfo}/>
                    }
                    {
                        !data.project.scmCommitInfo && <Empty/>
                    }
                </LoadingContainer>
            </MainPage>
        </>
    )
}
