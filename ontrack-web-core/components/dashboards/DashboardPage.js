import MainPage from "@components/layouts/MainPage";
import JumpToProject from "@components/projects/JumpToProject";
import DashboardContextProvider from "@components/dashboards/DashboardContextProvider";
import DashboardView from "@components/dashboards/DashboardView";
import {Command, LegacyLinkCommand} from "@components/common/Commands";
import DashboardCommandMenu from "@components/dashboards/DashboardCommandMenu";
import GridTableContextProvider from "@components/grid/GridTableContext";
import {FaPlus} from "react-icons/fa";
import NewProjectCommand from "@components/projects/NewProjectCommand";

export default function DashboardPage({title}) {

    const commands = [
        <NewProjectCommand key="create-project"/>,
        <JumpToProject key="project"/>,
        <LegacyLinkCommand
            key="legacy"
            href={"/"}
            text="Legacy home"
            title="Goes to the legacy home page"
        />,
        <DashboardCommandMenu key="dashboard"/>,
    ]

    return (
        <>
            <GridTableContextProvider>
                <DashboardContextProvider>
                    <MainPage
                        title={title}
                        breadcrumbs={[]}
                        commands={commands}
                    >
                        <DashboardView/>
                    </MainPage>
                </DashboardContextProvider>
            </GridTableContextProvider>
        </>
    )
}