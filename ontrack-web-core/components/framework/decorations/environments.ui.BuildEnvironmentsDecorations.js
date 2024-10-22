import {Space, Tag} from "antd";
import Link from "next/link";
import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function BuildEnvironmentsDecorations({decoration}) {
    return (
        <>
            <Space>
                {
                    decoration.data.map((stub, index) => (
                        <Link key={index} href={slotUri({id: stub.slotId})}>
                            <Tag>{stub.environmentName}</Tag>
                        </Link>
                    ))
                }
            </Space>
        </>
    )
}