import {Space} from "antd";
import EnvironmentLink from "@components/extension/environments/EnvironmentLink";

export default function BuildEnvironmentsDecorations({decoration}) {
    return (
        <>
            <Space size={0}>
                {
                    decoration.data.map((stub, index) => (
                        <EnvironmentLink
                            slot={{
                                id: stub.slotId,
                                qualifier: stub.qualifier,
                                environment: {
                                    id: stub.environmentId,
                                    name: stub.environmentName,
                                }
                            }}
                        />
                    ))
                }
            </Space>
        </>
    )
}