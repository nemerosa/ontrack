import {Button, Form, Input, Space, Typography} from "antd";
import {prefixedFormName} from "@components/form/formUtils";
import {FaPlus, FaTrash} from "react-icons/fa";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.List name={prefixedFormName(prefix, 'links')}>
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
                                        style={{width: '100%'}}
                                    >
                                        <Form.Item>
                                            <FaTrash onClick={() => remove(name)}/>
                                        </Form.Item>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'name']}
                                            rules={[{required: true, message: 'Link name is required.',},]}
                                        >
                                            <Input placeholder="Name"/>
                                        </Form.Item>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'value']}
                                            rules={[{required: true, message: 'Link is required.',},]}
                                        >
                                            <Input placeholder="URL" style={{width: '30em'}}/>
                                        </Form.Item>
                                    </Space>
                                </>
                            ))}
                            <Button type="dashed" onClick={() => add()} block>
                                <Space>
                                    <FaPlus/>
                                    <Typography.Text>Add link</Typography.Text>
                                </Space>
                            </Button>
                        </div>
                    </>
                )}
            </Form.List>
        </>
    )
}