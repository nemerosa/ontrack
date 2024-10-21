import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input, Typography} from "antd";

export const useConfirmWithReason = ({onConfirm, question, reasonFieldLabel = "Reason"}) => {

    const dialog = useFormDialog({
        question,
        onSuccess: (values) => {
            const reason = values.reason
            if (onConfirm) onConfirm(reason)
        },
        reasonFieldLabel: reasonFieldLabel,
    })

    const cancelConfirm = () => {
        dialog.start({})
    }

    return [
        cancelConfirm,
        <ConfirmWithReasonDialog key="component" dialog={dialog}/>,
    ]
}

export function ConfirmWithReasonDialog({dialog}) {
    return (
        <>
            <FormDialog
                dialog={dialog}
            >
                <Form.Item>
                    <Typography.Text>{dialog.question}</Typography.Text>
                </Form.Item>
                <Form.Item
                    name="reason"
                    label={dialog.reasonFieldLabel}
                    rules={[
                        {
                            required: true,
                            message: 'Reason is required.',
                        },
                    ]}
                >
                    <Input/>
                </Form.Item>
            </FormDialog>
        </>
    )
}