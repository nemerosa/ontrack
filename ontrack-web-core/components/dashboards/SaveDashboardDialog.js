import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Alert, Form, Input, Select} from "antd";
import {saveDashboardQuery} from "@components/dashboards/DashboardConstants";

export function useSaveDashboardDialog() {
    return useFormDialog({
        init: (form, dashboard) => {
            form.setFieldsValue({
                name: dashboard.name,
            })
        },
        prepareValues: (values, dashboard) => {
            values.context = dashboard.context
            values.contextId = dashboard.contextId
            values.key = dashboard.key
            values.layoutKey = dashboard.layoutKey
            values.widgets = dashboard.widgets
        },
        query: saveDashboardQuery,
        userNode: 'saveDashboard',
    })
}

export function SaveDashboardDialog({saveDashboardDialog}) {
    return (
        <>
            <FormDialog dialog={saveDashboardDialog}>
                {
                    saveDashboardDialog?.context?.message && <Form.Item>
                        <Alert type="warning" showIcon={true} message={saveDashboardDialog.context.message}/>
                    </Form.Item>
                }
                <Form.Item name="name"
                           label="Name"
                           rules={[
                               {
                                   required: true,
                                   message: 'Dashboard name is required.',
                               },
                               {
                                   max: 80,
                                   type: 'string',
                                   message: 'Dashboard name must be 80 characters long at a maximum.',
                               },
                           ]}
                >
                    <Input placeholder="Dashboard name" allowClear/>
                </Form.Item>
                <Form.Item name="userScope"
                           label="User scope"
                >
                    <Select
                        defaultValue="USER"
                        options={[
                            {
                                value: 'USER',
                                label: "For current user"
                            },
                            {
                                value: 'GLOBAL',
                                label: "For all users"
                            },
                        ]}
                    />
                </Form.Item>
                <Form.Item name="contextScope"
                           label="Context scope"
                >
                    <Select
                        defaultValue="ID"
                        options={[
                            {
                                value: 'ID',
                                label: "For current page"
                            },
                            {
                                value: 'CONTEXT',
                                label: "For all similar pages"
                            },
                        ]}
                    />
                </Form.Item>
            </FormDialog>
        </>
    )

}