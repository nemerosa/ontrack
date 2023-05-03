import {useState} from "react";
import {Form, Input, Modal, Switch} from "antd";

export function useNewProjectDialog() {
    const [open, setOpen] = useState(false)
    return {
        open,
        setOpen,
        start: () => {
            // TODO Reset data
            // Opens the dialog
            setOpen(true)
        }
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
                    >
                        <Switch/>
                    </Form.Item>
                </Form>
            </Modal>
        </>
    )
}