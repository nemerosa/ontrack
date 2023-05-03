import {useState} from "react";
import {Form, Input, Modal, Switch} from "antd";
import graphQLCall, {getUserErrors} from "@client/graphQLCall";
import {gql} from "graphql-request";
import FormErrors from "@components/form/FormErrors";

export function useNewProjectDialog({onSuccess}) {
    const [open, setOpen] = useState(false)
    return {
        open,
        setOpen,
        start: () => {
            // TODO Reset data
            // Opens the dialog
            setOpen(true)
        },
        onSuccess: onSuccess,
    }
}

export default function NewProjectDialog({newProjectDialog}) {

    const [form] = Form.useForm()
    const [formErrors, setFormErrors] = useState([]);
    const [loading, setLoading] = useState(false)

    const onCancel = () => {
        newProjectDialog.setOpen(false)
    }

    const onSubmit = async () => {
        setLoading(true)
        setFormErrors([])
        try {
            const values = await form.validateFields()
            values.description = values.description ? values.description : ''
            values.disabled = values.disabled ? values.disabled : false
            const data = await graphQLCall(
                gql`
                    mutation CreateProject(
                        $name: String!,
                        $description: String,
                        $disabled: Boolean!,
                    ) {
                        createProject(input: {
                            name: $name,
                            description: $description,
                            disabled: $disabled,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `,
                values
            )
            const errors = getUserErrors(data.createProject)
            if (errors) {
                setFormErrors(errors)
            } else {
                newProjectDialog.setOpen(false)
                if (newProjectDialog.onSuccess) {
                    newProjectDialog.onSuccess()
                }
            }
        } catch (ignored) {
            //
        } finally {
            setLoading(false)
        }
    }

    return (
        <>
            <Modal
                open={newProjectDialog.open}
                closable={false}
                confirmLoading={loading}
                onCancel={onCancel}
                onOk={onSubmit}
            >
                <Form
                    layout="vertical"
                    form={form}
                >
                    <Form.Item name="name"
                               label="Name"
                               rules={[
                                   {
                                       required: true,
                                       message: 'Project name is required.',
                                   },
                                   {
                                       max: 80,
                                       type: 'string',
                                       message: 'Project name must be 80 characters long at a maximum.',
                                   },
                                   {
                                       pattern: /[A-Za-z0-9._-]+/,
                                       message: 'Project name must contain only letters, digits, dots, underscores or dashes.',
                                   },
                               ]}
                    >
                        <Input placeholder="Project name" allowClear/>
                    </Form.Item>
                    <Form.Item name="description"
                               label="Description"
                               rules={[
                                   {
                                       max: 500,
                                       type: 'string',
                                       message: 'Project description must be 500 characters long at a maximum.',
                                   },
                               ]}
                    >
                        <Input placeholder="Project description" allowClear/>
                    </Form.Item>
                    <Form.Item name="disabled"
                               label="Disabled"
                               valuePropName="checked"
                    >
                        <Switch/>
                    </Form.Item>
                </Form>
                <FormErrors errors={formErrors}/>
            </Modal>
        </>
    )
}