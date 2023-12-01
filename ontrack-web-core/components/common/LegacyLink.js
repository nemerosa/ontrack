import Link from "next/link";
import LegacyIndicator from "@components/common/LegacyIndicator";
import {useConnection} from "@components/providers/ConnectionContextProvider";

export default function LegacyLink({href, children}) {

    const {environment} = useConnection()

    return (
        <Link href={`${environment?.ontrack?.url}${href}`}>
            <LegacyIndicator>
                {children}
            </LegacyIndicator>
        </Link>
    )
}