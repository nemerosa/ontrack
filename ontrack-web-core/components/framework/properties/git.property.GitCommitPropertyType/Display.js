import {Typography} from "antd";

export default function Display({property}) {

    return (
        <>
            <Typography.Text code copyable>{property.value.commit}</Typography.Text>
        </>
    )
}