import {Space, Typography} from "antd";
import SelectProject from "@components/projects/SelectProject";
import SelectBranch from "@components/branches/SelectBranch";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import SelectPromotionLevel from "@components/promotionLevels/SelectPromotionLevel";

export default function SelectProjectBranchPromotionLevel({value, onChange}) {

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

    const setPromotionLevelName = (name) => {
        if (value?.project && value?.branch) {
            onChange({
                ...value,
                promotionLevel: name,
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
                <SelectPromotionLevel
                    disabled={!value?.project || !value?.branch || !branch}
                    branch={branch}
                    useName={true}
                    allowClear={true}
                    value={value?.promotionLevel}
                    onChange={setPromotionLevelName}
                />
            </Space>
        </>
    )
}