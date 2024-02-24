import {Tag} from "antd";

export default function Display({property}) {

    return (
        <>
            <Tag color="green">{property.value.name}</Tag>
        </>
    )
}