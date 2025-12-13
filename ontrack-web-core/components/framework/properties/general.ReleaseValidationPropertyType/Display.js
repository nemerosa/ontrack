import {Tag} from "antd";

export default function Display({property}) {

    return (
        <>
            <Tag>{property.value?.validation}</Tag>
        </>
    )
}