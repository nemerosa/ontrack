import {FaPencil} from "react-icons/fa6";
import InlineCommand from "@components/common/InlineCommand";
import WebhookDialog, {useWebhookDialog} from "@components/extension/notifications/webhooks/WebhookDialog";

export default function WebhookEditCommand({webhook, onSuccess}) {

    const dialog = useWebhookDialog({onSuccess})

    const editWebhook = () => {
        dialog.start({webhook})
    }

    return (
        <>
            <InlineCommand
                icon={<FaPencil/>}
                title="Edit webhook"
                onClick={editWebhook}
            />
            <WebhookDialog webhookDialog={dialog}/>
        </>
    )
}