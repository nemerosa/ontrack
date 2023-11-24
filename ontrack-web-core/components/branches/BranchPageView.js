import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {CloseCommand, Command, LegacyLinkCommand} from "@components/common/Commands";
import {branchLegacyUri, branchUri, projectUri} from "@components/common/Links";
import Head from "next/head";
import {branchTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {branchBreadcrumbs} from "@components/common/Breadcrumbs";
import LoadingContainer from "@components/common/LoadingContainer";
import {gql} from "graphql-request";
import BranchInfoViewDrawer from "@components/branches/BranchInfoViewDrawer";
import {Skeleton, Space} from "antd";
import BranchFavourite from "@components/branches/BranchFavourite";
import {gqlInformationFragment, gqlPropertiesFragment} from "@components/services/fragments";
import {getBranchViews} from "@components/branches/views/branchViews";
import {usePreferences} from "@components/providers/PreferencesProvider";
import {useRouter} from "next/router";
import JumpToBranch from "@components/branches/JumpToBranch";
import {FaLink} from "react-icons/fa";

export default function BranchPageView({id}) {
    const [loadingBranch, setLoadingBranch] = useState(true)
    const [branch, setBranch] = useState({project: {}})
    const [commands, setCommands] = useState([])

    const [branchViews, setBranchViews] = useState([])

    const defaultEmptyView = {
        key: 'none',
        component: <Skeleton/>,
    }

    const preferences = usePreferences()

    const [selectedBranchView, setSelectedBranchView] = useState(defaultEmptyView)

    const router = useRouter()

    const onBranchViewSelected = (branchView) => {
        setSelectedBranchView(branchView)
        preferences.setPreferences({
            selectedBranchViewKey: branchView.key,
        })
        // noinspection JSIgnoredPromiseFromCall
        router.replace({
            pathname: `/branch/${id}`,
            query: {selectedBranchViewKey: branchView.key}
        }, undefined, {shallow: true})
    }

    const [initialSelectedViewKey, setInitialSelectedViewKey] = useState('')
    useEffect(() => {
        // By default, use the classic view
        let key = 'classic'
        // Gets the initialSelectedViewKey from the preferences
        key = preferences.selectedBranchViewKey ? preferences.selectedBranchViewKey : key
        // Gets the initialSelectedViewKey from the permalink
        const {selectedBranchViewKey} = router.query
        key = selectedBranchViewKey ? selectedBranchViewKey : key
        // Logging
        // console.log("Selected branch view key: ", key)
        // Selection of the view
        const branchView = branchViews.find(branchView => branchView.key === key) || defaultEmptyView
        setInitialSelectedViewKey(branchView.key)
        setSelectedBranchView(branchView)
    }, [branch, branchViews]);

    useEffect(() => {
        if (id) {
            setLoadingBranch(true)
            graphQLCall(
                gql`
                    query GetBranch($id: Int!) {
                        branches(id: $id) {
                            id
                            name
                            project {
                                id
                                name
                            }
                            authorizations {
                                name
                                action
                                authorized
                            }
                            properties {
                                ...propertiesFragment
                            }
                            information {
                                ...informationFragment
                            }
                        }
                    }
                    ${gqlPropertiesFragment}
                    ${gqlInformationFragment}
                `,
                {id}
            ).then(data => {
                let branch = data.branches[0];
                setBranch(branch)
                let loadedBranchViews = getBranchViews(branch);
                setBranchViews(loadedBranchViews)
                setLoadingBranch(false)
                setCommands([
                    // TODO Since only one view for now (classic), not allowing to change views
                    // <BranchViewSelector
                    //     branchViews={loadedBranchViews}
                    //     initialSelectedViewKey={initialSelectedViewKey}
                    //     onBranchViewSelected={onBranchViewSelected}
                    // />,
                    <JumpToBranch key="branch" projectName={branch.project.name}/>,
                    <Command
                        key="links"
                        icon={<FaLink/>}
                        href={`${branchUri(branch)}/links`}
                        text="Links"
                        title="Displays downstream and upstream dependencies"
                    />,
                    <LegacyLinkCommand
                        key="legacy"
                        href={branchLegacyUri(branch)}
                        text="Legacy branch"
                        title="Goes to the legacy branch page"
                    />,
                    <CloseCommand key="close" href={projectUri(branch.project)}/>,
                ])
            })
        }
    }, [id])

    return (
        <>
            <Head>
                {branchTitle(branch)}
            </Head>
            <MainPage
                title={
                    <Space>
                        {branch.name}
                        <BranchFavourite branch={branch}/>
                    </Space>
                }
                breadcrumbs={branchBreadcrumbs(branch)}
                commands={commands}
            >
                <LoadingContainer loading={loadingBranch} tip="Loading branch">
                    <BranchInfoViewDrawer branch={branch} loadingBranch={loadingBranch}/>
                    {selectedBranchView.component}
                </LoadingContainer>
            </MainPage>
        </>
    )
}