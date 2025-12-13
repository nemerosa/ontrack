import {Form, Input, InputNumber, Switch} from "antd";
import {prefixedFormName} from "@components/form/formUtils";
import SelectBuildGitCommitLink from "@components/extension/git/SelectBuildGitCommitLink";
import SubformSelection from "@components/form/SubformSelection";
import BuildGitCommitLinkConfig from "@components/extension/git/BuildGitCommitLinkConfig";

export default function PropertyForm({prefix, form}) {

    return (
        <>
            <Form.Item
                label="Branch"
                name={prefixedFormName(prefix, 'branch')}
                rules={[{required: true, message: 'Branch is required.'}]}
            >
                <Input/>
            </Form.Item>
            <SubformSelection
                form={form}
                idLabel="Commit link"
                idName={prefixedFormName(prefix, ['buildCommitLink', 'id'])}
                idItem={<SelectBuildGitCommitLink/>}
                formLabel="Commit link configuration"
                formItem={commitLinkId => <BuildGitCommitLinkConfig
                    prefix={prefixedFormName(prefix, ['buildCommitLink', 'data'])}
                    commitLinkId={commitLinkId}
                />}
            />
            <Form.Item
                label="Override"
                name={prefixedFormName(prefix, 'override')}
                extra="Build overriding policy when synchronizing"
            >
                <Switch/>
            </Form.Item>
            <Form.Item
                label="Tag interval"
                name={prefixedFormName(prefix, 'buildTagInterval')}
                extra="Interval in minutes for build/tag synchronization"
            >
                <InputNumber min={0}/>
            </Form.Item>
        </>
    )
}