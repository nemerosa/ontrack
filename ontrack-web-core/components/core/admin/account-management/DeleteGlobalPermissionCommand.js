import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import {
    useMutationDeleteGlobalPermissionAccount,
    useMutationDeleteGlobalPermissionAccountGroup
} from "@components/core/admin/account-management/GlobalPermissionsService";

export default function DeleteGlobalPermissionCommand({refresh, globalPermission}) {

    const {
        deleteGlobalPermissionAccount,
        loading: loadingAccount
    } = useMutationDeleteGlobalPermissionAccount({onSuccess: refresh})
    const {
        deleteGlobalPermissionAccountGroup,
        loading: loadingAccountGroup
    } = useMutationDeleteGlobalPermissionAccountGroup({onSuccess: refresh})

    const onConfirm = async () => {
        if (globalPermission.target.type === 'ACCOUNT') {
            await deleteGlobalPermissionAccount({accountId: globalPermission.target.id})
        } else {
            await deleteGlobalPermissionAccountGroup({accountGroupId: globalPermission.target.id})
        }
    }

    return (
        <>
            <InlineConfirmCommand
                title="Delete this global permission"
                confirm="Do you want to delete this global permission?"
                onConfirm={onConfirm}
            />
        </>
    )
}
