import {Button, Form, Input, Space, Typography} from "antd";
import {prefixedFormName} from "@components/form/formUtils";
import {FaPlus, FaTrash} from "react-icons/fa";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.List name={prefixedFormName(prefix, 'patterns')}>
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
                                            label={<FaTrash onClick={() => remove(name)}/>}
                                            rules={[{required: true, message: 'Pattern name is required.',},]}
                                        >
                                            <Input placeholder="Name"/>
                                        </Form.Item>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'value']}
                                            label="Regex"
                                            rules={[{required: true, message: 'Pattern value is required.',},]}
                                        >
                                            <Input placeholder="Value"/>
                                        </Form.Item>
                                    </Space>
                                </>
                            ))}
                            <Button type="dashed" onClick={() => add()} block>
                                <Space>
                                    <FaPlus/>
                                    <Typography.Text>Add pattern</Typography.Text>
                                </Space>
                            </Button>
                        </div>
                    </>
                )}
            </Form.List>
        </>
    )
}