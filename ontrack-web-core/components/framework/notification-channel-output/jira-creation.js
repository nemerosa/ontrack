import Link from "next/link";

export default function JiraCreationNotificationChannelOutput({key, url}) {
    return (
        <>
            <Link href={url}>{key}</Link>
        </>
    )
}