import InlineCommand from "@components/common/InlineCommand";
import {FaEye} from "react-icons/fa";
import {useMutation} from "@components/services/GraphQL";
import {gql} from "graphql-request";

export default function WebhookTestCommand({webhook}) {

    const {mutate, loading} = useMutation(
        gql`
            mutation TestWebhook($name: String!) {
                testWebhook(input: {
                    name: $name
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'testWebhook',
        },
    )

    const testWebhook = async () => {
        await mutate({name: webhook.name})
    }

    return (
        <>
            <InlineCommand
                icon={<FaEye/>}
                title="Sending a test payload to this webhook"
                onClick={testWebhook}
                loading={loading}
            />
        </>
    )
}