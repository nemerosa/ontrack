import {Typography} from "antd";

export default function Display({property}) {

    return (
        <>
            <Typography.Text code>{property.value.branch}</Typography.Text>
        </>
    )
}