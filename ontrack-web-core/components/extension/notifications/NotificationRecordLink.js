import Link from "next/link";
import {FaLink} from "react-icons/fa";

export default function NotificationRecordLink({recordId}) {
    return (
        <>
            <Link
                href={`/extension/notifications/recordings/${recordId}`}
                title="Link to notification record"
            >
                <FaLink/>
            </Link>
        </>
    )
}