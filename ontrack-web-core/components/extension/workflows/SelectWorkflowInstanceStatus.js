import {useWorkflowInstanceStatus} from "@components/extension/workflows/WorkflowInstanceStatusHook";
import {useEffect, useState} from "react";
import WorkflowInstanceStatus from "@components/extension/workflows/WorkflowInstanceStatus";
import {Select} from "antd";

export default function SelectWorkflowInstanceStatus({value, onChange}) {

    const {data, loading} = useWorkflowInstanceStatus()

    const [options, setOptions] = useState([])
    useEffect(() => {
        if (data) {
            setOptions(
                data.map(status => ({
                    value: status,
                    label: <WorkflowInstanceStatus status={status}/>,
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