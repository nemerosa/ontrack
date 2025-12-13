import {Popconfirm} from "antd";
import {Command} from "@components/common/Commands";
import {FaTrashAlt} from "react-icons/fa";
import {useMutationDeleteAccount} from "@components/core/admin/account-management/AccountManagementService";

export default function DeleteAccountCommand({account, refresh}) {

    const {deleteAccount, loading} = useMutationDeleteAccount({
        onSuccess: refresh,
    })

    const onDeleteAccount = async () => {
        await deleteAccount({accountId: Number(account.id)})
    }

    return (
        <Popconfirm
            title="Account deletion"
            description="Are you sure you want to delete this account?"
            onConfirm={onDeleteAccount}
        >
            <div>
                <Command
                    icon={<FaTrashAlt/>}
                    title="Delete this account"
                    disabled={loading}
                />
            </div>
        </Popconfirm>
    )
}