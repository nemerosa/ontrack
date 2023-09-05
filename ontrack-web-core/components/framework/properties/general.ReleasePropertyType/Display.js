import {Typography} from "antd";

export default function Display({property}) {

    return (
        <>
            <Typography.Text>{property.value.name}</Typography.Text>
        </>
    )
}