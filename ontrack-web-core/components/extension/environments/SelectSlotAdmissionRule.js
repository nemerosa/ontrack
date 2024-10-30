import {useSlotAdmissionRules} from "@components/extension/environments/SlotAdmissionRules";
import {useEffect, useState} from "react";
import {Select} from "antd";

export default function SelectSlotAdmissionRule({id = "selectSlotAdmissionRule", value, onChange}) {

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
                id={id}
                data-testid={id}
                value={value}
                onChange={onChange}
                options={options}
                allowClear={true}
            />
        </>
    )
}