import {Select} from "antd";
import {useQuery} from "@components/services/GraphQL";
import {useState} from "react";
import {gql} from "graphql-request";

export default function SelectPromotionLevelName({value, onChange}) {

    const [token, setToken] = useState('')

    const {data: promotionLevelNames, loading} = useQuery(
        gql`
            query PromotionLevelNames($token: String!) {
               promotionLevelNames(token: $token)
            }
        `,
        {
            variables: {token},
            deps: [token],
            condition: token && token.length >= 2,
            dataFn: data => data.promotionLevelNames,
        }
    )

    const onSearch = (value) => {
        setToken(value)
    }

    const onSelect = (value) => {
        if (onChange) onChange(value || '')
    }

    const onClear = () => {
        setToken('')
        if (onChange) onChange('')
    }

    return (
        <Select
            showSearch
            value={value}
            placeholder="Promotion level name"
            defaultActiveFirstOption={false}
            suffixIcon={null}
            filterOption={false}
            onSearch={onSearch}
            onChange={onSelect}
            allowClear
            onClear={onClear}
            loading={loading}
            notFoundContent={null}
            options={(promotionLevelNames || []).map(name => ({
                value: name,
                label: name,
            }))}
            style={{width: '20em'}}
        />
    )
}