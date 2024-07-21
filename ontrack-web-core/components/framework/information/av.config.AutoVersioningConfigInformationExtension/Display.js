import Link from "next/link";
import {branchAutoVersioningUri} from "@components/common/Links";

export default function AutoVersioningConfigInformationExtension({entity}) {
    return <Link href={branchAutoVersioningUri(entity)}>Configuration</Link>
}