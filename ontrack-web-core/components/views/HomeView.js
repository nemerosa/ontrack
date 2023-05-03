import MainPage from "@components/layouts/MainPage";
import ProjectList from "@components/projects/ProjectList";
import {Command} from "@components/common/Commands";
import {PlusOutlined} from "@ant-design/icons";
import NewProjectDialog, {useNewProjectDialog} from "@components/projects/NewProjectDialog";

export default function HomeView() {

    const newProjectDialog = useNewProjectDialog()
    const newProject = () => {
        newProjectDialog.start()
    }

    const commands = [
        <Command
            icon={<PlusOutlined/>}
            text="New project"
            action={newProject}
        />,
    ]

    return (
        <>
            <MainPage
                title="Home"
                commands={commands}
            >
                <ProjectList/>
            </MainPage>
            <NewProjectDialog
                newProjectDialog={newProjectDialog}
            />
        </>
    )
}