import {Select} from "antd";

export default function SelectAutoVersioningAuditRunningState({value, onChange}) {

    const options = [
        {
            value: "true",
            label: "Running",
        },
        {
            value: "false",
            label: "Finished",
        },
    ]

    return (
        <>
            <Select
                options={options}
                value={value}
                onChange={onChange}
                style={{
                    width: '10em',
                }}
            />
        </>
    )
}