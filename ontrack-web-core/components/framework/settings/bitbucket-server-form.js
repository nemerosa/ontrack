import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form} from "antd";
import DurationPicker from "@components/common/DurationPicker";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="autoMergeTimeout"
                    label="Auto merge timeout"
                    extra="Maximum duration to wait for an auto-merge"
                >
                    <DurationPicker inMilliseconds={true} maxUnit="hour"/>
                </Form.Item>
                <Form.Item
                    name="autoMergeInterval"
                    label="Auto merge interval"
                    extra="Maximum duration to wait between each auto-merge check"
                >
                    <DurationPicker inMilliseconds={true} maxUnit="hour"/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}