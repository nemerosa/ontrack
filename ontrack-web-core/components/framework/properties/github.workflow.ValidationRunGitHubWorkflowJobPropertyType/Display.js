import {Space, Spin, Typography} from "antd";
import Link from "next/link";
import {FaGithub} from "react-icons/fa";

export default function Display({property}) {

    return (
        <>
            <Space key={property.value.runId}>
                <FaGithub/>
                <Link href={property.value.url}>
                    {property.value.name}#{property.value.runNumber}
                </Link>
                <Typography.Text code>{property.value.job}</Typography.Text>
                {property.value.event && <Typography.Text code>{property.value.event}</Typography.Text>}
                {
                    property.value.running && <>
                        <Spin size="small"/>
                        Running
                    </>
                }
            </Space>
        </>
    )
}
