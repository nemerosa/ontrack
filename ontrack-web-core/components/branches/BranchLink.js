import Link from "next/link";
import {branchUri} from "@components/common/Links";

export default function BranchLink({branch, text}) {
    return (
        <>
            <Link href={branchUri(branch)}>{text ? text : branch.name}</Link>
        </>
    )
}