import {Space} from "antd";
import Link from "next/link";
import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";

export default function BuildEnvironmentsDecorations({decoration}) {
    return (
        <>
            <Space size={0}>
                {
                    decoration.data.map((stub, index) => (
                        <Link
                            key={index}
                            href={slotUri({id: stub.slotId})}
                        >
                            <EnvironmentIcon
                                environmentId={stub.environmentId}
                                tooltipText={
                                    <>
                                        Deployed in {stub.environmentName}
                                        {
                                            stub.qualifier &&
                                            ` [${stub.qualifier}]`
                                        }
                                    </>
                                }
                            />
                        </Link>
                    ))
                }
            </Space>
        </>
    )
}