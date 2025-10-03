import {useQuery} from "@components/services/useQuery";
import {Select} from "antd";

export default function GraphQLEnumSelect({
                                              id,
                                              value,
                                              onChange,
                                              mode,
                                              query,
                                              queryNode,
                                          }) {
    const {data: options, loading} = useQuery(
        query,
        {
            initialData: [],
            dataFn: data => data[queryNode].map(it => ({
                value: it,
                label: it,
            }))
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
                mode={mode}
            />
        </>
    )
}