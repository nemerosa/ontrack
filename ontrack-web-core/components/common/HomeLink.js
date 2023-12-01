import Link from "next/link";
import {homeUri} from "@components/common/Links";

export default function HomeLink({text}) {
    return (
        <>
            <Link href={homeUri()}>{text ? text : "Home"}</Link>
        </>
    )
}