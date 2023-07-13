import MultipleSelectSearch from "@components/common/MultipleSelectSearch";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export default function SelectMultiplePromotionLevelNames({value, onChange}) {
    const fetchPromotionNames = async (token) => {
        return graphQLCall(
            gql`
                query PromotionLevelNames($token: String!) {
                    promotionLevelNames(token: $token)
                }
            `, {token}
        ).then(data => data.promotionLevelNames.map(name => ({
            value: name,
            label: name,
        })))
    }

    return (
        <>
            <MultipleSelectSearch
                mode="multiple"
                value={value}
                placeholder="Select promotions"
                fetchOptions={fetchPromotionNames}
                onChange={(newValue) => {
                    const values = newValue ? newValue.map(it => it.value) : []
                    if (onChange) onChange(values)
                }}
            />
        </>
    )
}