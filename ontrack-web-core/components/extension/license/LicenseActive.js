import {Tag} from "antd";

export default function LicenseActive({active}) {
    return (
        <>
            {
                active && <Tag color="success">Active</Tag>
            }
            {
                !active && <Tag color="error">Not active</Tag>
            }
        </>
    )
}