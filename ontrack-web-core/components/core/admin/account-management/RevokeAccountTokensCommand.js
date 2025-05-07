import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import {useRevokeAccountTokens} from "@components/core/admin/account-management/AccountManagementService";

export default function RevokeAccountTokensCommand({accountId, onSuccess}) {

    const {revokeAccountTokens, loading} = useRevokeAccountTokens({onSuccess})

    const revoke = async () => {
        await revokeAccountTokens({accountId: Number(accountId)})
    }

    return (
        <>
            <InlineConfirmCommand
                title="Revoke all tokens for this account"
                confirm="Do you want to revoke all tokens for this account? Access using these tokens will be impossible."
                onConfirm={revoke}
                loading={loading}
            />
        </>
    )
}