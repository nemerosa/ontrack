import MainPage from "@components/layouts/MainPage";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import Head from "next/head";
import {projectTitle} from "@components/common/Titles";
import {projectBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand, LegacyLinkCommand} from "@components/common/Commands";
import {homeUri, legacyProjectUri} from "@components/common/Links";
import {
    gqlDecorationFragment,
    gqlInformationFragment,
    gqlPropertiesFragment,
    gqlUserMenuActionFragment
} from "@components/services/fragments";
import PageSection from "@components/common/PageSection";
import BranchList from "@components/branches/BranchList";
import {Empty, Space} from "antd";
import RowTag from "@components/common/RowTag";
import BranchBox from "@components/branches/BranchBox";
import JumpToBranch from "@components/branches/JumpToBranch";
import ProjectFavourite from "@components/projects/ProjectFavourite";
import {useEventForRefresh} from "@components/common/EventsContext";
import ProjectInfoViewDrawer from "@components/projects/ProjectInfoViewDrawer";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import UserMenuActions from "@components/entities/UserMenuActions";
import {gqlProjectContentFragment} from "@components/projects/ProjectGraphQLFragments";
import {isAuthorized} from "@components/common/authorizations";
import DisableProjectCommand from "@components/projects/DisableProjectCommand";
import DisabledProjectBanner from "@components/projects/DisabledProjectBanner";
import {gqlBranchContentFragment} from "@components/branches/BranchGraphQLFragments";
import NewBranchCommand from "@components/branches/NewBranchCommand";
import ProjectEnvironmentsCommand from "@components/extension/environments/project/ProjectEnvironmentsCommand";

export default function ProjectView({id}) {

    const client = useGraphQLClient()

    const [loadingProject, setLoadingProject] = useState(true)

    const [project, setProject] = useState({})
    const [branches, setBranches] = useState([])
    const [favouriteBranches, setFavouriteBranches] = useState([])
    const [commands, setCommands] = useState([])

    const favouriteRefreshCount = useEventForRefresh("branch.favourite")
    const projectUpdated = useEventForRefresh("project.updated")
    const branchCreated = useEventForRefresh("branch.created")

    useEffect(() => {
        if (id && client) {
            setLoadingProject(true)
            client.request(
                gql`
                    query GetProject($id: Int!) {
                        project(id: $id) {
                            ...ProjectContent
                            properties {
                                ...propertiesFragment
                            }
                            information {
                                ...informationFragment
                            }
                            userMenuActions {
                                ...userMenuActionFragment
                            }
                            authorizations {
                                name
                                action
                                authorized
                            }
                            branches(order: true, count: 6) {
                                ...BranchContent
                                favourite
                                decorations {
                                    ...decorationContent
                                }
                            }
                            favouriteBranches: branches(favourite: true, order: true) {
                                ...BranchContent
                                favourite
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
                    ${gqlUserMenuActionFragment}
                    ${gqlProjectContentFragment}
                    ${gqlBranchContentFragment}
                `,
                {id}
            ).then(data => {
                const project = data.project
                setProject(project)
                setFavouriteBranches(project.favouriteBranches)
                setBranches(project.branches)

                // Commands
                const commands = []
                // Commands depending on the project authorizations & state
                commands.push(
                    <NewBranchCommand
                        key="branch-create"
                        project={project}
                    />
                )
                if (isAuthorized(project, 'project', 'disable')) {
                    commands.push(
                        <DisableProjectCommand
                            key="disable-enable"
                            project={project}
                        />
                    )
                }
                // All the rest of commands
                commands.push(
                    <ProjectEnvironmentsCommand key="environments" id={project.id}/>,
                    <UserMenuActions key="userMenuActions" actions={project.userMenuActions}/>,
                    <JumpToBranch key="branch" projectName={project.name}/>,
                    <LegacyLinkCommand
                        key="legacy"
                        href={legacyProjectUri(project)}
                        text="Legacy project"
                        title="Goes to the legacy project page"
                    />,
                    <CloseCommand key="close" href={homeUri()}/>,
                )
                // Setting the commands
                setCommands(commands)
            }).finally(() => {
                setLoadingProject(false)
            })
        }
    }, [client, id, favouriteRefreshCount, projectUpdated, branchCreated])


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
                    <DisabledProjectBanner project={project}/>
                    {
                        favouriteBranches && favouriteBranches.length > 0 &&
                        <PageSection
                            loading={loadingProject}
                            title="Favourite branches"
                            height="250px"
                            padding={true}
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
                        height="300px"
                        padding={true}
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