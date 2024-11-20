import NewProjectDialog, {useNewProjectDialog} from "@components/projects/NewProjectDialog";
import {useContext} from "react";
import {EventsContext} from "@components/common/EventsContext";
import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";
import {UserContext} from "@components/providers/UserProvider";

export default function NewProjectCommand() {

    const user = useContext(UserContext)
    const eventsContext = useContext(EventsContext)

    const newProjectDialog = useNewProjectDialog({
        onSuccess: () => {
            eventsContext.fireEvent("project.created")
        }
    })

    const onCreateProject = () => {
        newProjectDialog.start()
    }

    return (
        <>
            {
                user.authorizations.project?.create && <>
                    <NewProjectDialog newProjectDialog={newProjectDialog}/>
                    <Command
                        icon={<FaPlus/>}
                        text="New project"
                        title="Creates a new project"
                        action={onCreateProject}
                    />
                </>
            }
        </>
    )
}