import Link from "next/link";
import LegacyIndicator from "@components/common/LegacyIndicator";

export default function LegacyLink({href, children}) {
    return (
        <Link href={href}>
            <LegacyIndicator>
                {children}
            </LegacyIndicator>
        </Link>
    )
}