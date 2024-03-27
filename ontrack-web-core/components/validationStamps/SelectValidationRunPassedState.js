import {Select} from "antd";

export default function SelectValidationRunPassedState({value, onChange}) {

    const options = [
        {
            value: "true",
            label: "Passed",
        },
        {
            value: "false",
            label: "Not passed",
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