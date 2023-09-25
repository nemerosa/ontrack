import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import SelectValidationStamp from "@components/validationStamps/SelectValidationStamp";

export function useEditValidationStampFilterDialog({onSuccess}) {
    return useFormDialog({
        init: (form, {filter}) => {
            form.setFieldValue('name', filter?.name)
            form.setFieldValue('vsNames', filter?.vsNames)
        },
        onSuccess: onSuccess,
    })
}

export default function EditValidationStampFilterDialog({branch, editValidationStampFilterDialog}) {
    return (
        <>
            <FormDialog dialog={editValidationStampFilterDialog}>
                <Form.Item
                    name="name"
                    label="Name"
                    initialValue={editValidationStampFilterDialog?.context?.filter?.name}
                >
                    <Input disabled/>
                </Form.Item>
                <Form.Item
                    name="vsNames"
                    label="Validation stamps"
                >
                    <SelectValidationStamp
                        branch={branch}
                        multiple={true}
                        allowClear={true}
                        useName={true}
                    />
                </Form.Item>
            </FormDialog>
        </>
    )
}
