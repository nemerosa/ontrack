import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {gql} from "graphql-request";
import {Alert, Form, Input} from "antd";

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
            // TODO From the input
            values.userScope = "GLOBAL"
            // TODO From the input
            values.contextScope = "CONTEXT"
            // Widgets
            values.widgets = dashboard.widgets
        },
        query: gql`
            mutation SaveDashboard(
                $context: String!,
                $contextId: String!,
                $userScope: DashboardContextUserScope!,
                $contextScope: DashboardContextScope!,
                $key: String,
                $name: String!,
                $layoutKey: String!,
                $widgets: [WidgetInstanceInput!]!,
            ) {
                saveDashboard(input: {
                    context: $context,
                    contextId: $contextId,
                    userScope: $userScope,
                    contextScope: $contextScope,
                    key: $key,
                    name: $name,
                    layoutKey: $layoutKey,
                    widgets: $widgets,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
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
            </FormDialog>
        </>
    )

}