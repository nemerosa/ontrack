import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import {useMutationDeleteGroupMapping} from "@components/core/admin/account-management/GroupMappingsService";

export default function DeleteGroupMappingCommand({groupMapping, refresh}) {

    const {deleteGroupMapping, loading} = useMutationDeleteGroupMapping({onSuccess: refresh})

    const onDelete = async () => {
        await deleteGroupMapping(groupMapping)
    }

    return (
        <>
            <InlineConfirmCommand
                title="Delete this group mapping"
                confirm="Do you want to delete this group mapping?"
                onConfirm={onDelete}
            />
        </>
    )
}