import {FaPlus} from "react-icons/fa";
import {Command} from "@components/common/Commands";
import {useContext} from "react";
import {isAuthorized} from "@components/common/authorizations";
import {EventsContext} from "@components/common/EventsContext";
import NewBranchDialog, {useNewBranchDialog} from "@components/branches/NewBranchDialog";

export default function NewBranchCommand({project}) {

    const eventsContext = useContext(EventsContext)

    const newBranchDialog = useNewBranchDialog({
        onSuccess: () => {
            eventsContext.fireEvent("branch.created")
        }
    })

    const onCreateBranch = () => {
        newBranchDialog.start({project})
    }

    return (
        <>
            {
                isAuthorized(project, 'branch', 'create') &&
                <>
                    <NewBranchDialog dialog={newBranchDialog}/>
                    <Command
                        icon={<FaPlus/>}
                        text="New branch"
                        title="Creates a new branch"
                        action={onCreateBranch}
                    />
                </>
            }
        </>
    )
}