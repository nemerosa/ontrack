import {Form, Modal} from "antd";
import {useState} from "react";
import graphQLCall, {getUserErrors} from "@client/graphQLCall";
import FormErrors from "@components/form/FormErrors";

export function useFormDialog(config) {
    const [open, setOpen] = useState(false)
    const [form] = Form.useForm()
    const [context, setContext] = useState(undefined)
    return {
        ...config,
        open,
        setOpen,
        form,
        context,
        start: (context) => {
            setContext(context)
            if (config.init) {
                config.init(form, context)
            }
            setOpen(true)
        },
    }
}

export default function FormDialog({dialog, onValuesChange, children}) {

    const form = dialog.form
    const [formErrors, setFormErrors] = useState([]);
    const [loading, setLoading] = useState(false)

    const onCancel = () => {
        form.resetFields()
        dialog.setOpen(false)
    }

    const onSubmit = async () => {
        setLoading(true)
        setFormErrors([])
        try {
            const values = await form.validateFields()
            if (dialog.prepareValues) dialog.prepareValues(values, dialog.context)
            const data = await graphQLCall(
                dialog.query,
                values
            )
            const result = data[dialog.userNode]
            const errors = getUserErrors(result)
            if (errors) {
                setFormErrors(errors)
            } else {
                dialog.setOpen(false)
                form.resetFields()
                if (dialog.onSuccess) {
                    dialog.onSuccess(result)
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
                open={dialog.open}
                closable={false}
                confirmLoading={loading}
                onCancel={onCancel}
                onOk={onSubmit}
            >
                <Form
                    layout="vertical"
                    form={form}
                    onValuesChange={onValuesChange}
                >
                    {children}
                </Form>
                <FormErrors errors={formErrors}/>
            </Modal>
        </>
    )
}