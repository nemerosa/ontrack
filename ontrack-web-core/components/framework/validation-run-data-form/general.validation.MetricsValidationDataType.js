import {Button, Form, Input, InputNumber, Space, Typography} from "antd";
import {FaPlus, FaTrash} from "react-icons/fa";

export default function MetricsValidationDataType() {
    return (
        <>
            <Form.List name={['data', 'metrics']}>
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
                                    <Space
                                        key={key}
                                    >
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'name']}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: 'Metric name is required.',
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
                                                    message: 'Metric value is required.',
                                                },
                                            ]}
                                        >
                                            <InputNumber placeholder="Value" stringMode={true}/>
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
                                    <Typography.Text>Add metric</Typography.Text>
                                </Space>
                            </Button>
                        </div>
                    </>
                )}
            </Form.List>
        </>
    )
}