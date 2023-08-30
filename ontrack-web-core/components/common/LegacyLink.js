import {Badge} from "antd";
import Link from "next/link";

export default function LegacyLink({href, children}) {
    return (
        <Link href={href}>
            <Badge status="warning" dot title="Link to the legacy UI">
                {children}
            </Badge>
        </Link>
    )
}