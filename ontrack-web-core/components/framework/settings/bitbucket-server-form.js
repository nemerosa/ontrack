import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, InputNumber, Switch} from "antd";
import DurationPicker from "@components/common/DurationPicker";

export default function BitbucketServerForm({id, ...values}) {
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
                <Form.Item
                    name="maxCommits"
                    label="Max commits"
                    extra="Maximum number of commits to return for a change log"
                >
                    <InputNumber min={1} max={10000}/>
                </Form.Item>
                <Form.Item
                    name="autoDeleteBranch"
                    label="Auto delete branch"
                    extra="Deleting a branch after auto-versioning is completed"
                >
                    <Switch/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}