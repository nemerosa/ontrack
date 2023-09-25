import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";

export function useNewValidationStampFilterDialog({onSuccess}) {
    return useFormDialog({
        onSuccess: onSuccess,
    })
}

export default function NewValidationStampFilterDialog({newValidationStampFilterDialog}) {
    return (
        <>
            <FormDialog dialog={newValidationStampFilterDialog}>
                <Form.Item
                    name="name"
                    label="Name"
                    rules={[
                        {
                            required: true,
                            message: 'Filter name is required.',
                        },
                    ]}
                >
                    <Input/>
                </Form.Item>
            </FormDialog>
        </>
    )
}