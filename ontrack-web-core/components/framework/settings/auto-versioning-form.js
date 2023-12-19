import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Switch} from "antd";
import DurationPicker from "@components/common/DurationPicker";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="enabled"
                    label="Enabled"
                    extra="Check to enable auto-versioning in Ontrack."
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="auditRetentionDuration"
                    label="Audit retention duration"
                    extra="Maximum duration to keep audit entries for active auto-versioning requests"
                >
                    <DurationPicker/>
                </Form.Item>
                <Form.Item
                    name="auditCleanupDuration"
                    label="Audit cleanup duration"
                    extra="Maximum duration to keep audit entries for all kinds of auto-versioning requests (counted after the audit retention)"
                >
                    <DurationPicker/>
                </Form.Item>
                <Form.Item
                    name="buildLinks"
                    label="Build links"
                    extra="Check to enable the creation of build links on auto-versioning."
                >
                    <Switch/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}