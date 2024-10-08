import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input, InputNumber} from "antd";
import SelectWorkflowNodeExecutor from "@components/extension/workflows/SelectWorkflowNodeExecutor";
import {useState} from "react";
import Well from "@components/common/Well";
import WorkflowNodeExecutorForm from "@components/extension/workflows/WorkflowNodeExecutorForm";

export const useConfigureWorkflowNodeDialog = ({onSuccess}) => {

    const [currentValues, setCurrentValues] = useState({})

    return useFormDialog({
        init: (form, context) => {
            setCurrentValues({...context})
            form.setFieldValue('id', context.id)
            form.setFieldValue('description', context.description)
            form.setFieldValue('executorId', context.executorId)
            form.setFieldValue('timeout', context.timeout)
            form.setFieldValue('data', context.data)
        },
        currentValues,
        setCurrentValues,
        onSuccess,
    })
}

export default function ConfigureWorkflowNodeDialog({dialog}) {

    const onValuesChange = (changedValues /*, allValues*/) => {
        if (changedValues.hasOwnProperty('executorId')) {
            const executorId = changedValues.executorId
            if (executorId !== dialog.currentValues.executorId) {
                dialog.form.setFieldValue('data', null)
                dialog.setCurrentValues({...dialog.currentValues, executorId, data: null})
            }
        }
    }

    return (
        <>
            <FormDialog dialog={dialog} onValuesChange={onValuesChange}>
                <Form.Item
                    name="id"
                    label="ID"
                    extra="Unique ID of this node in the workflow"
                    rules={[{required: true, message: 'Node ID is required',},]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="description"
                    label="Description"
                    extra="Description of this node in the workflow"
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="executorId"
                    label="Executor"
                    extra="How this done will be executed?"
                    rules={[{required: true, message: 'Executor is required',},]}
                >
                    <SelectWorkflowNodeExecutor/>
                </Form.Item>
                <Form.Item
                    name="timeout"
                    label="Timeout (s)"
                    extra="Timeout (in seconds) for the execution of this node"
                    rules={[{required: true, message: 'Timeout is required',},]}
                    initialValue={300}
                >
                    <InputNumber min={1}/>
                </Form.Item>
                {
                    dialog.currentValues.executorId &&
                    <Form.Item
                        name="data"
                        label="Configuration"
                    >
                        <Well>
                            <WorkflowNodeExecutorForm
                                executorId={dialog.currentValues.executorId}
                            />
                        </Well>
                    </Form.Item>
                }
            </FormDialog>
        </>
    )
}