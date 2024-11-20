import {isAuthorized} from "@components/common/authorizations";
import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";
import {useContext} from "react";
import {EventsContext} from "@components/common/EventsContext";
import NewBuildDialog, {useNewBuildDialog} from "@components/builds/NewBuildDialog";

export default function NewBuildCommand({branch}) {

    const eventsContext = useContext(EventsContext)

    const newBuildDialog = useNewBuildDialog({
        onSuccess: () => {
            eventsContext.fireEvent("build.created")
        }
    })

    const onCreateBuild = () => {
        newBuildDialog.start({branch})
    }

    return (
        <>
            {
                isAuthorized(branch, 'build', 'create') &&
                <>
                    <NewBuildDialog dialog={newBuildDialog}/>
                    <Command
                        icon={<FaPlus/>}
                        text="New build"
                        title="Creates a new build"
                        action={onCreateBuild}
                    />
                </>
            }
        </>
    )
}