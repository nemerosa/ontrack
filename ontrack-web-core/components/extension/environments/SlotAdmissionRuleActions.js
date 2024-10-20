import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useState} from "react";
import {gql} from "graphql-request";
import {message} from "antd";
import {processGraphQLErrors} from "@components/services/graphql-utils";

export default function SlotAdmissionRuleActions({id, onChange}) {

    const [messageApi, contextHolder] = message.useMessage()
    const client = useGraphQLClient()
    const [loading, setLoading] = useState(false)

    const deleteRule = async () => {
        setLoading(true)
        try {
            const data = await client.request(
                gql`
                    mutation DeleteSlotAdmissionRuleConfig($id: String!) {
                        deleteSlotAdmissionRuleConfig(input: {
                            id: $id
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `,
                {id}
            )
            if (processGraphQLErrors(data, 'deleteSlotAdmissionRuleConfig', messageApi)) {
                if (onChange) onChange()
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <>
            {contextHolder}
            <InlineConfirmCommand
                title="Delete this rule"
                confirm="Do you really want to delete this rule?"
                onConfirm={deleteRule}
                loading={loading}
            />
        </>
    )
}