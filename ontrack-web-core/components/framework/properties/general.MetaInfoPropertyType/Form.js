import {Button, Form, Input, Space, Typography} from "antd";
import {prefixedFormName} from "@components/form/formUtils";
import {FaPlus, FaTrash} from "react-icons/fa";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.List name={prefixedFormName(prefix, 'items')}>
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
                                            rules={[{required: true, message: 'Meta info name is required.',},]}
                                        >
                                            <Input placeholder="Name"/>
                                        </Form.Item>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'value']}
                                        >
                                            <Input placeholder="Value"/>
                                        </Form.Item>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'link']}
                                        >
                                            <Input placeholder="Link"/>
                                        </Form.Item>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'category']}
                                        >
                                            <Input placeholder="Category"/>
                                        </Form.Item>
                                    </Space>
                                </>
                            ))}
                            <Button type="dashed" onClick={() => add()} block>
                                <Space>
                                    <FaPlus/>
                                    <Typography.Text>Add meta info</Typography.Text>
                                </Space>
                            </Button>
                        </div>
                    </>
                )}
            </Form.List>
        </>
    )
}