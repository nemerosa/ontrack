import {Form, Modal} from "antd";
import {useState} from "react";
import FormErrors from "@components/form/FormErrors";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {getUserErrors} from "@components/services/graphql-utils";

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

    const client = useGraphQLClient()

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
            let result
            let errors = undefined
            if (dialog.query) {
                const data = await client.request(
                    dialog.query,
                    values
                )
                result = data[dialog.userNode]
                errors = getUserErrors(result)
            } else {
                result = values
            }
            if (errors) {
                setFormErrors(errors)
            } else {
                dialog.setOpen(false)
                form.resetFields()
                if (dialog.onSuccess) {
                    dialog.onSuccess(result, dialog.context)
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