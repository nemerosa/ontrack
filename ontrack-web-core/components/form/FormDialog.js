import {Button, Form, Modal, Space} from "antd";
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

export default function FormDialog({dialog, onValuesChange, children, hasOk = true, submittable = true, header, width, height, extraButtons, okText}) {

    const client = useGraphQLClient()

    const form = dialog.form
    const [formErrors, setFormErrors] = useState([]);
    const [loading, setLoading] = useState(false)

    const bodyStyle = {}
    if (height) {
        bodyStyle.height = height
        bodyStyle.overflowY = 'auto'
        bodyStyle.overflowX = 'hidden'
    }

    const onCancel = () => {
        form.resetFields()
        dialog.setOpen(false)
    }

    const getActualQuery = () => {
        if (dialog.query) {
            if (typeof dialog.query === 'function') {
                return dialog.query(dialog.context)
            } else {
                return dialog.query
            }
        } else {
            return undefined
        }
    }

    const getActualUserNode = () => {
        if (dialog.userNode) {
            if (typeof dialog.userNode === 'function') {
                return dialog.userNode(dialog.context)
            } else {
                return dialog.userNode
            }
        } else {
            return undefined
        }
    }

    const onSubmit = async () => {
        setLoading(true)
        setFormErrors([])
        try {
            let values = await form.validateFields()
            if (dialog.prepareValues) {
                values = await dialog.prepareValues(values, dialog.context)
            }
            let result
            let errors
            const actualQuery = getActualQuery()
            if (actualQuery) {
                const data = await client.request(
                    actualQuery,
                    values
                )
                const actualUserNode = getActualUserNode()
                result = data[actualUserNode]
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
                footer={null}
                width={width}
            >
                <Form
                    layout="vertical"
                    form={form}
                    onFinish={onSubmit}
                    onValuesChange={onValuesChange}
                >
                    <Space direction="vertical" className="ot-line">
                        {
                            header &&
                            <div style={{width: '100%'}}>
                                {header}
                            </div>
                        }
                        <div style={bodyStyle}>
                            {children}
                        </div>
                        <Form.Item>
                            <Space style={{float: 'right'}}>
                                {
                                    extraButtons
                                }
                                <Button type="default" onClick={onCancel}>
                                    Cancel
                                </Button>
                                {
                                    hasOk &&
                                    <Button type="primary" htmlType="submit" disabled={!submittable}>
                                        {okText ?? "OK"}
                                    </Button>
                                }
                            </Space>
                        </Form.Item>
                    </Space>
                </Form>
                <FormErrors errors={formErrors}/>
            </Modal>
        </>
    )
}