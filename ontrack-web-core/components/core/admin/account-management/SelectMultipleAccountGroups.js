import {useEffect, useState} from "react";
import {useAccountGroups} from "@components/core/admin/account-management/AccountManagementService";
import {Select} from "antd";

export default function SelectMultipleAccountGroups({id, value, onChange, mode = "multiple"}) {

    const [name, setName] = useState('')
    const {groups, loading} = useAccountGroups({name})
    const [options, setOptions] = useState([])

    useEffect(() => {
        setOptions(groups.map(group => ({
            value: group.id,
            label: group.name,
        })))
    }, [groups])

    const handleChange = (newValue) => {
        if (onChange) onChange(newValue)
    }

    const handleSearch = async (token) => {
        if (token && token.length > 2) {
            setName(token)
        } else {
            setOptions([])
        }
    }

    const handleClear = () => {
        setOptions([])
        if (onChange) onChange(null)
    }

    return (
        <>
            <Select
                id={id}
                data-testid={id}
                showSearch={true}
                value={value}
                defaultActiveFirstOption={true}
                suffixIcon={null}
                filterOption={false}
                onSearch={handleSearch}
                onChange={handleChange}
                loading={loading}
                allowClear={true}
                onClear={handleClear}
                notFoundContent={null}
                options={options}
                mode={mode}
                style={{
                    width: '16em',
                }}
            />
        </>
    )
}