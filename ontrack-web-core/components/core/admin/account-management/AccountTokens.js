import {Space, Typography} from "antd";
import {FaBan, FaList} from "react-icons/fa";
import RevokeAccountTokensCommand from "@components/core/admin/account-management/RevokeAccountTokensCommand";

/**
 * Displaying the number of tokens for an account and associated actions.
 *
 * @param accountId ID of the account
 * @param tokens List of tokens
 * @param refresh Callback to refresh the list of accounts
 */
export default function AccountTokens({accountId, tokens, refresh}) {
    return (
        <>
            {
                tokens.length > 0 &&
                <Space>
                    <Space title={`${tokens.length} token(s) for this account`}>
                        <FaList/>
                        <Typography.Text>{tokens.length}</Typography.Text>
                    </Space>
                    <RevokeAccountTokensCommand accountId={accountId} onSuccess={refresh}/>
                </Space>
            }
            {
                tokens.length === 0 &&
                <Typography.Text type="secondary">
                    <FaBan title="No token for this account"/>
                </Typography.Text>
            }
        </>
    )
}