import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import {useMutation} from "@components/services/GraphQL";
import {gql} from "graphql-request";

export default function WebhookDeleteCommand({webhook, onSuccess}) {

    const {mutate, loading,} = useMutation(
        gql`
            mutation DeleteWebhook($name: String!) {
                deleteWebhook(input: {
                    name: $name
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'deleteWebhook',
            onSuccess,
        },
    )

    const deleteWebhook = async () => {
        await mutate({name: webhook.name})
    }

    return (
        <>
            <InlineConfirmCommand
                title="Deleting the webhook"
                loading={loading}
                confirm={`Are you sure to delete the webhook ${webhook.name}?`}
                onConfirm={deleteWebhook}
            />
        </>
    )
}