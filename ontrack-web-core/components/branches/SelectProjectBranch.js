import {Space, Typography} from "antd";
import SelectProject from "@components/projects/SelectProject";
import SelectBranch from "@components/branches/SelectBranch";

export default function SelectProjectBranch({value, onChange}) {

    const setProjectName = (name) => {
        onChange({
            ...value,
            project: name,
        })
    }

    const setBranchName = (name) => {
        if (value?.project) {
            onChange({
                ...value,
                branch: name,
            })
        }
    }

    return (
        <>
            <Space>
                <Typography.Text>Project</Typography.Text>
                <SelectProject value={value?.project} onChange={setProjectName}/>
                <Typography.Text>Branch</Typography.Text>
                <SelectBranch project={value?.project}
                              value={value?.branch}
                              onChange={setBranchName}
                              disabled={!value?.project}
                />
            </Space>
        </>
    )
}