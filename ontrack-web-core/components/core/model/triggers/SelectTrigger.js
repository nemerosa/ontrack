import {useEffect, useState} from "react";
import {Select} from "antd";
import {useTriggers} from "@components/core/model/triggers/TriggerHook";

export default function SelectTrigger({value, onChange}) {

    const {data, loading} = useTriggers()

    const [options, setOptions] = useState([])
    useEffect(() => {
        if (data) {
            setOptions(
                data.map(({id, displayName}) => ({
                    value: id,
                    label: displayName,
                }))
            )
        }
    }, [data])

    return (
        <>
            <Select
                value={value}
                onChange={onChange}
                allowClear={true}
                loading={loading}
                options={options}
                style={{width: "12em"}}
            />
        </>
    )
}