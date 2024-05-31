import {Tag} from "antd";

export default function Display({property}) {
    return (
        <>
            {
                property.value.linkNames.map(link => (
                    <>
                        <Tag key={link}>{link}</Tag>
                    </>
                ))
            }
        </>
    )
}