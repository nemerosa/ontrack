import {validationRunUri} from "@components/common/Links";
import {Typography} from "antd";
import Link from "next/link";

export default function ValidationRunLink({run, text}) {
    return (
        <>
            <Link href={validationRunUri(run)}>
                <Typography.Text>{text ? text : `#${run.runOrder}`}</Typography.Text>
            </Link>
        </>
    )
}