import {Select} from "antd";

export default function ValidationRunSortingMode({value, onChange}) {
    return (
        <Select
            value={value}
            options={[
                {
                    value: "ID",
                    label: "From the newest"
                },
                {
                    value: "RUN_TIME",
                    label: "From the slowest"
                },
                {
                    value: "NAME",
                    label: "Alphabetical order"
                }
            ]}
            onChange={onChange}
        />
    )
}