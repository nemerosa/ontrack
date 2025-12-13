import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";
import WebhookDialog, {useWebhookDialog} from "@components/extension/notifications/webhooks/WebhookDialog";

export default function WebhookCreateCommand({onSuccess}) {

    const dialog = useWebhookDialog({onSuccess})

    const createWebhook = () => {
        dialog.start({creation: true})
    }

    return (
        <>
            <Command
                icon={<FaPlus/>}
                text="Create a webhook"
                action={createWebhook}
            />
            <WebhookDialog webhookDialog={dialog}/>
        </>
    )
}