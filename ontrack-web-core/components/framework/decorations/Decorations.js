import {Space} from "antd";

export default function Decorations({entity}) {
    return (
        <>
            {
                entity.decorations && <Space size={8}>
                    {/* TODO */}
                </Space>
            }
        </>
    )
}