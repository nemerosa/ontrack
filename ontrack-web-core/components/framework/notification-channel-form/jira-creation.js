import {Button, Form, Input, Select, Space, Typography} from "antd";
import {FaExclamationTriangle, FaPlus} from "react-icons/fa";

export default function JiraCreationNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={[prefix, "configName"]}
                label="Config"
                extra="Name of the Jira configuration to use for the connection."
                rules={[{required: true, message: 'Config name is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={[prefix, "projectName"]}
                label="Project"
                extra="Name of the Jira project where to create the ticket."
                rules={[{required: true, message: 'Project name is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={[prefix, "issueType"]}
                label="Issue type"
                extra="Name of the Jira issue type to use for the ticket."
                rules={[{required: true, message: 'Issue type is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={[prefix, "labels"]}
                label="Labels"
                extra="List of labels to set on the ticket"
            >
                <Select
                    mode="tags"
                    style={{width: '100%'}}
                    placeholder="List of labels"
                />
            </Form.Item>
            <Form.Item
                name={[prefix, "fixVersion"]}
                label="Fix version"
                extra="Name of the fix version to set. Can be a template"
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={[prefix, "assignee"]}
                label="Assignee"
                extra="Username of the assignee to the ticket."
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={[prefix, "titleTemplate"]}
                label="Title"
                extra="Template for the ticket's summary"
                rules={[{required: true, message: 'Title is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.List
                name={[prefix, "customFields"]}
                label="Custom fields"
                extra="List of custom fields to create in the ticket."
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
                                    <Space
                                        key={key}
                                        direction="vertical"
                                        className="ot-form-list-item"
                                        align="baseline"
                                    >
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'name']}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: 'Field name is required.',
                                                },
                                            ]}
                                        >
                                            <Input placeholder="Field name"/>
                                        </Form.Item>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'value']}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: 'Field value is required.',
                                                },
                                            ]}
                                        >
                                            <Input.TextArea
                                                rows={4}
                                                placeholder="Field value"
                                            />
                                        </Form.Item>
                                        <Typography.Text>
                                            <FaExclamationTriangle color="orange"/>
                                            &nbsp;
                                            This <b>must</b> be a JSON representation,
                                            even for simple text values. Text values themselves can be
                                            templated.
                                        </Typography.Text>
                                    </Space>
                                </>
                            ))}
                            <Button type="dashed" onClick={() => add()} block>
                                <Space>
                                    <FaPlus/>
                                    <Typography.Text>Add custom field</Typography.Text>
                                </Space>
                            </Button>
                        </div>
                    </>
                )}
            </Form.List>
        </>
    )
}