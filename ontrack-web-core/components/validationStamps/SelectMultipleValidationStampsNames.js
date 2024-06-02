import MultipleSelectSearch from "@components/common/MultipleSelectSearch";
import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function SelectMultipleValidationStampsNames({value, onChange}) {

    const client = useGraphQLClient()
    const fetchValidationNames = async (token) => {
        return client.request(
            gql`
                query ValidationStampNames($token: String!) {
                    validationStampNames(token: $token)
                }
            `, {token}
        ).then(data => data.validationStampNames.map(name => ({
            value: name,
            label: name,
        })))
    }

    return (
        <>
            <MultipleSelectSearch
                mode="multiple"
                value={value}
                placeholder="Select validations"
                fetchOptions={fetchValidationNames}
                onChange={(newValue) => {
                    const values = newValue ? newValue.map(it => it.value) : []
                    if (onChange) onChange(values)
                }}
            />
        </>
    )
}