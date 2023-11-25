import MainPage from "@components/layouts/MainPage";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import Head from "next/head";
import {projectTitle} from "@components/common/Titles";
import {projectBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand, LegacyLinkCommand} from "@components/common/Commands";
import {homeUri, projectLegacyUri} from "@components/common/Links";
import {gqlDecorationFragment, gqlInformationFragment, gqlPropertiesFragment} from "@components/services/fragments";
import PageSection from "@components/common/PageSection";
import BranchList from "@components/branches/BranchList";
import {Empty, Space} from "antd";
import RowTag from "@components/common/RowTag";
import BranchBox from "@components/branches/BranchBox";
import JumpToBranch from "@components/branches/JumpToBranch";
import ProjectFavourite from "@components/projects/ProjectFavourite";
import {useDashboardEventForRefresh} from "@components/common/EventsContext";
import ProjectInfoViewDrawer from "@components/projects/ProjectInfoViewDrawer";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function ProjectView({id}) {

    const client = useGraphQLClient()

    const [loadingProject, setLoadingProject] = useState(true)

    const [project, setProject] = useState({})
    const [branches, setBranches] = useState([])
    const [favouriteBranches, setFavouriteBranches] = useState([])

    const favouriteRefreshCount = useDashboardEventForRefresh("branch.favourite")

    useEffect(() => {
        if (id && client) {
            setLoadingProject(true)
            client.request(
                gql`
                    query GetProject($id: Int!) {
                        projects(id: $id) {
                            id
                            name
                            properties {
                                ...propertiesFragment
                            }
                            information {
                                ...informationFragment
                            }
                            branches(order: true, count: 6) {
                                project {
                                    id
                                    name
                                }
                                id
                                name
                                favourite
                                decorations {
                                    ...decorationContent
                                }
                            }
                            favouriteBranches: branches(favourite: true, order: true) {
                                project {
                                    id
                                    name
                                }
                                id
                                name
                                favourite
                                disabled
                                decorations {
                                    ...decorationContent
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
                    ${gqlPropertiesFragment}
                    ${gqlInformationFragment}
                `,
                {id}
            ).then(data => {
                setProject(data.projects[0])
                setFavouriteBranches(data.projects[0].favouriteBranches)
                setBranches(data.projects[0].branches)
            }).finally(() => {
                setLoadingProject(false)
            })
        }
    }, [client, id, favouriteRefreshCount])

    const commands = [
        <JumpToBranch key="branch" projectName={project.name}/>,
        <LegacyLinkCommand
            key="legacy"
            href={projectLegacyUri(project)}
            text="Legacy project"
            title="Goes to the legacy project page"
        />,
        <CloseCommand key="close" href={homeUri()}/>
    ]

    return (
        <>
            <Head>
                {projectTitle(project)}
            </Head>
            <MainPage
                title={
                    <Space>
                        {project.name}
                        <ProjectFavourite project={project}/>
                    </Space>
                }
                breadcrumbs={projectBreadcrumbs(project)}
                commands={commands}
            >
                <Space direction="vertical" className="ot-line" size={16}>
                    {
                        favouriteBranches && favouriteBranches.length > 0 &&
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
                        {
                            (!branches || branches.length === 0) &&
                            <Empty
                                image={Empty.PRESENTED_IMAGE_SIMPLE}
                                description="No branch has been created for this project"
                            />
                        }
                        {
                            branches && branches.length > 0 &&
                            <Space direction="horizontal" size={16} wrap>
                                {
                                    branches.map(branch => <RowTag key={branch.id}>
                                            <BranchBox branch={branch}/>
                                        </RowTag>
                                    )
                                }
                            </Space>
                        }
                    </PageSection>
                    <ProjectInfoViewDrawer project={project} loadingProject={loadingProject}/>
                </Space>
            </MainPage>
        </>
    )
}