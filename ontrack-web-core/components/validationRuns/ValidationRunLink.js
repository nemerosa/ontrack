import {validationRunUri} from "@components/common/Links";
import LegacyLink from "@components/common/LegacyLink";
import {Typography} from "antd";

export default function ValidationRunLink({run, text}) {
    return (
        <>
            <LegacyLink href={validationRunUri(run)}>
                <Typography.Text>{text ? text : `#${run.runOrder}`}</Typography.Text>
            </LegacyLink>
        </>
    )
}