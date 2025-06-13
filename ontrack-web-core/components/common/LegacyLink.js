import Link from "next/link";
import LegacyIndicator from "@components/common/LegacyIndicator";

/**
 * @deprecated Will be removed in V5
 */
export default function LegacyLink({href, children}) {
    return (
        <Link href={`/`}>
            <LegacyIndicator>
                {children}
            </LegacyIndicator>
        </Link>
    )
}