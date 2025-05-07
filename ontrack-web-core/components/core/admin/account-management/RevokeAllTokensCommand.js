import {Popconfirm} from "antd";
import {Command} from "@components/common/Commands";
import {FaBan} from "react-icons/fa";
import {useMutationRevokeAllTokens} from "@components/core/admin/account-management/AccountManagementService";

export default function RevokeAllTokensCommand({refresh}) {

    const {revokeAllTokens, loading} = useMutationRevokeAllTokens({refresh})

    return (
        <>
            <Popconfirm
                title="Revoking all API tokens"
                description="Are you sure to revoke all existing API tokens?"
                onConfirm={revokeAllTokens}
            >
                <div>
                    <Command
                        icon={<FaBan/>}
                        title="Revokes tokens for all accounts"
                        text="Revoke all tokens"
                        disabled={loading}
                    />
                </div>
            </Popconfirm>
        </>
    )
}