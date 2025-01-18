import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {Select} from "antd";

export default function SelectQueueRecordState({value, onChange}) {

    const {data: options, loading} = useQuery(
        gql`
            query QueueStates {
                queueRecordFilterInfo {
                    states
                }
            }
        `,
        {
            dataFn: data => {
                const options = [
                    {
                        value: '',
                        label: 'Any',
                    }
                ]
                options.push(
                    ...data.queueRecordFilterInfo.states.map(it => ({
                        value: it,
                        label: it,
                    }))
                )
                return options
            }
        }
    )

    return (
        <>
            <Select
                loading={loading}
                options={options}
                value={value}
                onChange={onChange}
                style={{
                    width: "10em",
                }}
            />
        </>
    )

}