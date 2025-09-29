import Link from "next/link";

export const webhookListUri = () => `/extension/notifications/webhooks`

export default function WebhookListLink() {
    return (
        <>
            <Link href={webhookListUri()}>Webhooks</Link>
        </>
    )
}
