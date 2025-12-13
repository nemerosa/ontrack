import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {Select} from "antd";

export default function SelectHookState({id, value, onChange}) {
    const {data: options, loading} = useQuery(
        gql`
            query HookStates {
                hookRecordFilterInfo {
                    states
                }
            }
        `,
        {
            initialData: [],
            dataFn: data => data.hookRecordFilterInfo?.states?.map(hook => ({
                value: hook,
                label: hook,
            })) ?? []
        }
    )

    return (
        <>
            <Select
                id={id}
                value={value}
                onChange={onChange}
                allowClear
                style={{width: '12em'}}
                options={options}
                loading={loading}
            />
        </>
    )
}