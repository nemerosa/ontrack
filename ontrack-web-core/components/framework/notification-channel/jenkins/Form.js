import {Button, Form, Input, InputNumber, Select, Space, Typography} from "antd";
import {FaPlus, FaTrash} from "react-icons/fa";
import {prefixedFormName} from "@components/form/formUtils";

export default function JenkinsNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'config')}
                label="Config"
                extra="Name of the Jenkins configuration to use for the connection."
                rules={[{required: true, message: 'Config name is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={prefixedFormName(prefix, 'job')}
                label="Job"
                extra="Path to the Jenkins job to call."
                rules={[{required: true, message: 'Job path is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.List
                name={prefixedFormName(prefix, 'parameters')}
                label="Parameters"
                extra="Parameters to send to the job."
            >
                {(fields, {add, remove}) => (
                    <>
                        <div
                            style={{
                                display: 'flex',
                                rowGap: 16,
                                flexDirection: 'column',
                            }}
                        >
                            {fields.map(({key, name, ...restField}) => (
                                <>
                                    <Space key={key}>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'name']}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: 'Parameter name is required.',
                                                },
                                            ]}
                                        >
                                            <Input placeholder="Name"/>
                                        </Form.Item>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'value']}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: 'Parameter value is required.',
                                                },
                                            ]}
                                        >
                                            <Input placeholder="Value"/>
                                        </Form.Item>
                                        <FaTrash
                                            onClick={() => {
                                                remove(name)
                                            }}
                                        />
                                    </Space>
                                </>
                            ))}
                            <Button type="dashed" onClick={() => add()} block>
                                <Space>
                                    <FaPlus/>
                                    <Typography.Text>Add parameter</Typography.Text>
                                </Space>
                            </Button>
                        </div>
                    </>
                )}
            </Form.List>
            <Form.Item
                name={prefixedFormName(prefix, 'callMode')}
                label="Call mode"
                extra="How to call the Jenkins job."
                initialValue="ASYNC"
            >
                <SelectJenkinsNotificationChannelConfigCallMode/>
            </Form.Item>
            <Form.Item
                name={prefixedFormName(prefix, 'timeout')}
                label="Timeout"
                extra="Timeout in seconds"
                initialValue={30}
            >
                <InputNumber min={1} max={3600}/>
            </Form.Item>
        </>
    )
}

export function SelectJenkinsNotificationChannelConfigCallMode({value, onChange}) {
    const options = [
        {
            value: 'ASYNC',
            label: 'ASYNC - Fires and forgets',
        },
        {
            value: 'SYNC',
            label: 'SYNC - Waits for job completion',
        },
    ]

    return (
        <>
            <Select
                options={options}
                value={value}
                onChange={onChange}
            />
        </>
    )
}