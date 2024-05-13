import {useContext, useEffect, useState} from "react";
import {WorkflowNodeExecutorContext} from "@components/extension/workflows/WorkflowNodeExecutorContext";
import {Select} from "antd";

export default function SelectWorkflowNodeExecutor({value, onChange, width}) {

    const executors = useContext(WorkflowNodeExecutorContext)

    const [options, setOptions] = useState([])

    useEffect(() => {
        setOptions(executors.map(executor => ({
            value: executor.id,
            label: executor.displayName,
        })))
    }, [executors]);

    return (
        <>
            <Select
                value={value}
                onChange={onChange}
                options={options}
                allowClear={true}
                style={{width}}
            />
        </>
    )
}