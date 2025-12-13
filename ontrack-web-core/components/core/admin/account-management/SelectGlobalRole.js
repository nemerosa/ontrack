import {useGlobalRoles} from "@components/core/admin/account-management/RolesService";
import {useEffect, useState} from "react";
import {Select, Typography} from "antd";

export default function SelectGlobalRole({id, value, onChange}) {

    const {globalRoles, loading} = useGlobalRoles()

    const [options, setOptions] = useState([])
    useEffect(() => {
        setOptions(globalRoles.map(globalRole => ({
            value: globalRole.id,
            label: <Typography.Text title={globalRole.description}>{globalRole.name}</Typography.Text>,
        })))
    }, [globalRoles])

    return (
        <>
            <Select
                id={id}
                data-testid={id}
                value={value}
                onChange={onChange}
                options={options}
                loading={loading}
                showSearch={true}
                optionFilterProp="label"
                allowClear={true}
                style={{
                    width: '16em',
                }}
            />
        </>
    )
}