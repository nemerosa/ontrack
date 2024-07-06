import {Alert, Button, Form, Space, Switch, Typography} from "antd";
import SettingsForm from "@components/core/admin/settings/SettingsForm";

export default function GeneralSecurityForm({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="grantProjectViewToAll"
                    label="Grant project view to all"
                    extra="Any logged user will have access to all projects by default."
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="grantProjectParticipationToAll"
                    label="Grant project participation to all"
                    extra="Any logged user will have the right to add comments in any project."
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="builtInAuthenticationEnabled"
                    label="Built-in authentication"
                    extra={
                        <Typography.Text type="warning">
                            If unchecked, make sure you have other authentication means in place.
                        </Typography.Text>
                    }
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="grantDashboardEditionToAll"
                    label="Grants dashboard creation rights to all"
                    extra="Any logged user will have the right to create dashboards."
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="grantDashboardSharingToAll"
                    label="Grants dashboard sharing rights to all"
                    extra="Any logged user will have the right to shared their dashboards."
                >
                    <Switch/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}