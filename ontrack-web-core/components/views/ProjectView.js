import MainPage from "@components/layouts/MainPage";
import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import Head from "next/head";
import {projectTitle} from "@components/common/Titles";
import {projectBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import LoadingContainer from "@components/common/LoadingContainer";
import {gqlDecorationFragment} from "@components/services/fragments";
import PageSection from "@components/common/PageSection";
import BranchList from "@components/branches/BranchList";
import {Space} from "antd";

export default function ProjectView({id}) {

    const [loadingProject, setLoadingProject] = useState(true)

    const [project, setProject] = useState({})
    const [favouriteBranches, setFavouriteBranches] = useState([])

    useEffect(() => {
        if (id) {
            setLoadingProject(true)
            graphQLCall(
                gql`
                    query GetProject($id: Int!) {
                        projects(id: $id) {
                            id
                            name
                            favouriteBranches: branches(favourite: true, order: true) {
                                id
                                name
                                disabled
                                decorations {
                                    ...decorationContent
                                }
                                project {
                                    id
                                    name
                                }
                                latestBuild: builds(count: 1) {
                                    id
                                    name
                                }
                                promotionLevels {
                                    id
                                    name
                                    image
                                    promotionRuns(first: 1) {
                                        build {
                                            id
                                            name
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ${gqlDecorationFragment}
                `,
                {id}
            ).then(data => {
                setProject(data.projects[0])
                setFavouriteBranches(data.projects[0].favouriteBranches)
            }).finally(() => {
                setLoadingProject(false)
            })
        }
    }, [id])

    const commands = [
        <CloseCommand key="close" href={homeUri()}/>
    ]

    return (
        <>
            <Head>
                {projectTitle(project)}
            </Head>
            <MainPage
                title={project.name}
                breadcrumbs={projectBreadcrumbs(project)}
                commands={commands}
            >
                <Space direction="vertical" className="ot-line">
                    {favouriteBranches &&
                        <PageSection
                            loading={loadingProject}
                            title="Favourite branches"
                        >
                            <BranchList
                                branches={favouriteBranches}
                                showProject={false}
                            />
                        </PageSection>
                    }
                    <PageSection
                        loading={loadingProject}
                        title="Last active branches"
                    >

                    </PageSection>
                </Space>
            </MainPage>
        </>
    )
}