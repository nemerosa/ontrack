import {Popconfirm} from "antd";
import {Command} from "@components/common/Commands";
import {FaTrashAlt} from "react-icons/fa";
import {useMutationDeleteAccountGroup} from "@components/core/admin/account-management/AccountManagementService";

export default function DeleteAccountGroupCommand({accountGroup, refresh}) {

    const {deleteAccountGroup, loading} = useMutationDeleteAccountGroup({
        onSuccess: refresh,
    })

    const onDeleteAccountGroup = async () => {
        await deleteAccountGroup({accountGroupId: Number(accountGroup.id)})
    }

    return (
        <Popconfirm
            title="Account group deletion"
            description="Are you sure you want to delete this account group?"
            onConfirm={onDeleteAccountGroup}
        >
            <div>
                <Command
                    icon={<FaTrashAlt/>}
                    title="Delete this account group"
                    disabled={loading}
                />
            </div>
        </Popconfirm>
    )
}