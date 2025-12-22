import {Select} from "antd";
import {useQuery} from "@components/services/GraphQL";
import {useState} from "react";
import {gql} from "graphql-request";

export default function SelectValidationStampName({value, onChange}) {

    const [token, setToken] = useState('')

    const {data: validationStampNames, loading} = useQuery(
        gql`
            query ValidationStampNames($token: String!) {
               validationStampNames(token: $token)
            }
        `,
        {
            variables: {token},
            deps: [token],
            condition: token && token.length >= 2,
            dataFn: data => data.validationStampNames,
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
            placeholder="Validation stamp name"
            defaultActiveFirstOption={false}
            suffixIcon={null}
            filterOption={false}
            onSearch={onSearch}
            onChange={onSelect}
            allowClear
            onClear={onClear}
            loading={loading}
            notFoundContent={null}
            options={(validationStampNames || []).map(name => ({
                value: name,
                label: name,
            }))}
            style={{width: '20em'}}
        />
    )
}