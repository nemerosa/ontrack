import MainPage from "@components/layouts/MainPage";
import JumpToProject from "@components/projects/JumpToProject";
import DashboardContextProvider from "@components/dashboards/DashboardContextProvider";
import DashboardView from "@components/dashboards/DashboardView";
import {LegacyLinkCommand} from "@components/common/Commands";
import DashboardCommandMenu from "@components/dashboards/DashboardCommandMenu";

export default function DashboardPage({title}) {

    const commands = [
        <JumpToProject key="project"/>,
        <LegacyLinkCommand
            key="legacy"
            href={"/"}
            text="Legacy home"
            title="Goes to the legacy home page"
        />,
        <DashboardCommandMenu/>,
    ]

    return (
        <>
            <DashboardContextProvider>
                <MainPage
                    title={title}
                    breadcrumbs={[]}
                    commands={commands}
                >
                    <DashboardView/>
                </MainPage>
            </DashboardContextProvider>
        </>
    )
}