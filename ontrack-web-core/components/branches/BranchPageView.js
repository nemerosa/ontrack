import {useEffect, useState} from "react";
import {CloseCommand, Command} from "@components/common/Commands";
import {branchLinksUri, branchPromotionLevelsUri, projectUri} from "@components/common/Links";
import Head from "next/head";
import {branchTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {branchBreadcrumbs} from "@components/common/Breadcrumbs";
import LoadingContainer from "@components/common/LoadingContainer";
import BranchInfoViewDrawer from "@components/branches/BranchInfoViewDrawer";
import {Skeleton, Space} from "antd";
import BranchFavourite from "@components/branches/BranchFavourite";
import {getBranchViews} from "@components/branches/views/branchViews";
import {usePreferences} from "@components/providers/PreferencesProvider";
import {useRouter} from "next/router";
import JumpToBranch from "@components/branches/JumpToBranch";
import {FaMedal, FaProjectDiagram} from "react-icons/fa";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gqlGetBranch} from "@components/services/branches";
import UserMenuActions from "@components/entities/UserMenuActions";
import {isAuthorized} from "@components/common/authorizations";
import DisableBranchCommand from "@components/branches/DisableBranchCommand";
import {useEventForRefresh} from "@components/common/EventsContext";
import NewBuildCommand from "@components/builds/NewBuildCommand";

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
    const branchUpdated = useEventForRefresh("branch.updated")

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

    const client = useGraphQLClient()

    useEffect(() => {
        if (id && client) {
            client.request(
                gqlGetBranch,
                {id: Number(id)}
            ).then(data => {
                let branch = data.branches[0];
                setBranch(branch)
                let loadedBranchViews = getBranchViews(branch);
                setBranchViews(loadedBranchViews)
                const commands = []
                commands.push(
                    <NewBuildCommand
                        key="build-create"
                        branch={branch}
                    />
                )
                if (isAuthorized(branch, "branch", "disable")) {
                    commands.push(
                        <DisableBranchCommand
                            key="disable-enable"
                            branch={branch}
                        />
                    )
                }
                commands.push(
                    // TODO Since only one view for now (classic), not allowing to change views
                    // <BranchViewSelector
                    //     branchViews={loadedBranchViews}
                    //     initialSelectedViewKey={initialSelectedViewKey}
                    //     onBranchViewSelected={onBranchViewSelected}
                    // />,
                    <Command
                        key="promotionLevels"
                        icon={<FaMedal/>}
                        href={branchPromotionLevelsUri(branch)}
                        text="Promotions"
                        title="Management of the promotion levels for this branch"
                    />,
                    <UserMenuActions key="userMenuActions" actions={branch.userMenuActions}/>,
                    <JumpToBranch key="branch" projectName={branch.project.name}/>,
                    <Command
                        key="links"
                        icon={<FaProjectDiagram/>}
                        href={branchLinksUri(branch)}
                        text="Links"
                        title="Displays downstream and upstream dependencies"
                    />,
                    <CloseCommand key="close" href={projectUri(branch.project)}/>,
                )
                setCommands(commands)
            }).finally(() => {
                setLoadingBranch(false)
            })
        }
    }, [client, id, branchUpdated])

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