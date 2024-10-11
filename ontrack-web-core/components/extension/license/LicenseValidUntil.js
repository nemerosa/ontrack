import {Typography} from "antd";

export default function LicenseValidUntil({validUntil}) {
    return (
        <>
            {
                !validUntil && <Typography.Text>No validity limit.</Typography.Text>
            }
            {
                validUntil && <Typography.Text>{validUntil}</Typography.Text>
            }
        </>
    )
}