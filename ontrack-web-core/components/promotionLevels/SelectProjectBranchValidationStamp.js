import {Space} from "antd";
import SelectProject from "@components/projects/SelectProject";
import SelectBranch from "@components/branches/SelectBranch";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import SelectValidationStamp from "@components/validationStamps/SelectValidationStamp";

export default function SelectProjectBranchValidationStamp({value, onChange}) {

    const client = useGraphQLClient()

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

    const setValidationStampName = (name) => {
        if (value?.project && value?.branch) {
            onChange({
                ...value,
                validationStamp: name,
            })
        }
    }

    const [branch, setBranch] = useState()
    useEffect(() => {
        if (client && value?.project && value?.branch) {
            client.request(
                gql`
                    query BranchByName($project: String!, $branch: String!) {
                        branches(project: $project, name: $branch) {
                            id
                            name
                        }
                    }
                `,
                value
            ).then(data => {
                if (data.branches) {
                    setBranch(data.branches[0])
                }
            })
        } else {
            setBranch(null)
        }
    }, [client, value?.project, value?.branch]);

    return (
        <>
            <Space>
                <SelectProject
                    value={value?.project}
                    onChange={setProjectName}
                />
                <SelectBranch
                    project={value?.project}
                    value={value?.branch}
                    onChange={setBranchName}
                    disabled={!value?.project}
                />
                <SelectValidationStamp
                    disabled={!value?.project || !value?.branch || !branch}
                    branch={branch}
                    useName={true}
                    allowClear={true}
                    value={value?.validationStamp}
                    onChange={setValidationStampName}
                    width="16em"
                />
            </Space>
        </>
    )
}