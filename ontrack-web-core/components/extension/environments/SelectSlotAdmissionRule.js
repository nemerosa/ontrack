import {useSlotAdmissionRules} from "@components/extension/environments/SlotAdmissionRules";
import {useEffect, useState} from "react";
import {Select} from "antd";

export default function SelectSlotAdmissionRule({value, onChange}) {

    const rules = useSlotAdmissionRules()

    const [options, setOptions] = useState([])

    useEffect(() => {
        setOptions(rules.map(rule => ({
            value: rule.id,
            label: rule.name,
        })))
    }, [rules])

    return (
        <>
            <Select
                value={value}
                onChange={onChange}
                options={options}
                allowClear={true}
            />
        </>
    )
}