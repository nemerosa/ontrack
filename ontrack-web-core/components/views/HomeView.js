import MainPage from "@components/layouts/MainPage";
import ProjectList from "@components/projects/ProjectList";
import {Command} from "@components/common/Commands";
import {PlusOutlined} from "@ant-design/icons";

export default function HomeView() {

    const newProject = () => {
        alert("New project")
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
        </>
    )
}