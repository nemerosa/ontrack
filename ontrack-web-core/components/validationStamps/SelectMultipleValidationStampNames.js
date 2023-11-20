import MultipleSelectSearch from "@components/common/MultipleSelectSearch";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export default function SelectMultipleValidationStampNames({value, onChange}) {
    const fetchValidationNames = async (token) => {
        return graphQLCall(
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