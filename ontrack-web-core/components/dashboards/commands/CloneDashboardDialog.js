import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input, Select} from "antd";
import {saveDashboardQuery} from "@components/dashboards/DashboardConstants";
import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";

export function useCloneDashboardDialog(config) {
    return useFormDialog({
        ...config,
        init: (form, copy) => {
            form.setFieldsValue({
                name: copy.name,
            })
        },
        prepareValues: (values, copy) => {
            values.userScope = values.userScope ? values.userScope : "PRIVATE"
            values.uuid = copy.uuid
            values.layoutKey = copy.layoutKey
            values.widgets = copy.widgets
        },
        query: saveDashboardQuery,
        userNode: 'saveDashboard',
    })
}
export default function CloneDashboardDialog({cloneDashboardDialog}) {

    const user = useContext(UserContext)

    const options = [
        {
            value: 'PRIVATE',
            label: "Private use only"
        }
    ]
    if (user.authorizations?.dashboard?.share) {
        options.push({
            value: 'SHARED',
            label: "For all users"
        })
    }

    return (
        <>
            <FormDialog dialog={cloneDashboardDialog}>
                <Form.Item name="name"
                           label="Name"
                           rules={[
                               {
                                   required: true,
                                   message: 'Dashboard name is required.',
                               },
                           ]}
                >
                    <Input allowClear/>
                </Form.Item>
                <Form.Item name="userScope"
                           label="User scope"
                >
                    <Select
                        defaultValue="PRIVATE"
                        options={options}
                    />
                </Form.Item>
            </FormDialog>
        </>
    )
}