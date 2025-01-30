import {Select} from "antd";
import {useState} from "react";
import {gql} from "graphql-request";
import {useQuery} from "@components/services/useQuery";

export default function SelectPromotionLevelForProject({
                                                           id,
                                                           project,
                                                           value,
                                                           onChange,
                                                           placeholder = "Promotion level",
                                                           multiple = false,
                                                           width = '10em',
                                                       }) {

    const [token, setToken] = useState('')

    const {data, setData, loading} = useQuery(
        gql`
            query ProjectPromotionLevelNames($id: Int!, $token: String!) {
                project(id: $id) {
                    promotionLevelNames(token: $token)
                }
            }
        `,
        {
            variables: {
                id: project.id,
                token,
            },
            initialData: [],
            deps: [project.id, token],
            dataFn: data => data.project.promotionLevelNames.map(name => ({
                value: name,
                label: name,
            }))
        }
    )

    const handleSearch = (token) => {
        if (token && token.length > 2) {
            setToken(token)
        } else {
            setData([])
        }
    }

    const handleClear = () => {
        setData([])
        if (onChange) onChange(null)
    }

    return (
        <>
            <Select
                id={id}
                data-testid={id}
                showSearch={true}
                loading={loading}
                value={value}
                placeholder={placeholder}
                defaultActiveFirstOption={true}
                suffixIcon={null}
                filterOption={false}
                onSearch={handleSearch}
                onChange={onChange}
                allowClear={true}
                onClear={handleClear}
                notFoundContent={null}
                options={data}
                style={{
                    width: width,
                }}
                mode={multiple ? "multiple" : undefined}
            />
        </>
    )
}