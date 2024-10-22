import {Space, Tag} from "antd";
import Link from "next/link";
import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {FaServer} from "react-icons/fa";

export default function BuildEnvironmentsDecorations({decoration}) {
    return (
        <>
            <Space size={0}>
                {
                    decoration.data.map((stub, index) => (
                        <Link
                            key={index}
                            href={slotUri({id: stub.slotId})}
                            title={
                                stub.qualifier ?
                                    `Build deployed in ${stub.environmentName} [${stub.qualifier}] environment` :
                                    `Build deployed in ${stub.environmentName} environment`
                            }
                        >
                            <Tag>
                                <Space size={4}>
                                    <FaServer/>
                                    {stub.environmentName}
                                    {
                                        stub.qualifier &&
                                        `[${stub.qualifier}]`
                                    }
                                </Space>
                            </Tag>
                        </Link>
                    ))
                }
            </Space>
        </>
    )
}