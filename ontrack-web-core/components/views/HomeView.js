import MainPage from "@components/layouts/MainPage";
import Dashboard from "@components/dashboards/Dashboard";

export default function HomeView() {

    // const projectList = useProjectList()
    //
    // const newProjectDialog = useNewProjectDialog({
    //     onSuccess: () => {
    //         projectList.refresh()
    //     }
    // })
    // const newProject = () => {
    //     newProjectDialog.start()
    // }
    //
    // const commands = [
    //     <Command
    //         key="new-project"
    //         icon={<FaPlus/>}
    //         text="New project"
    //         action={newProject}
    //     />,
    // ]

    return (
        <>
            <MainPage
                title="Home"
                // commands={commands}
            >
                <Dashboard context="home"/>
            </MainPage>
            {/*<NewProjectDialog*/}
            {/*    newProjectDialog={newProjectDialog}*/}
            {/*/>*/}
        </>
    )
}