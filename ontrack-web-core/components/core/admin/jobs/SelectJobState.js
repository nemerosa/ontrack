import {Select} from "antd";
import {useRefData} from "@components/providers/RefDataProvider";
import JobState from "@components/core/admin/jobs/JobState";

export default function SelectJobState({value, onChange, allowClear, style, placeholder}) {

    const {jobStates} = useRefData()

    const options = jobStates.list.map(info => ({
        value: info.name,
        label: <JobState value={info.name} displayName={true} tooltip={false}/>,
    }))

    return (
        <>
            <Select
                options={options}
                value={value}
                onChange={onChange}
                allowClear={allowClear}
                style={style}
                placeholder={placeholder}
            />
        </>
    )
}