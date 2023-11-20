import {Drawer, FloatButton, Space} from "antd";
import {startTransition, useState} from "react";
import {FaInfoCircle} from "react-icons/fa";

export default function InfoViewDrawer({title, tooltip, width, children}) {

    const [expanded, setExpanded] = useState(false)

    const toggleExpanded = () => {
        startTransition(() => {
            setExpanded(!expanded)
        })
    }

    return (
        <>
            {
                !expanded && <FloatButton
                    icon={<FaInfoCircle/>}
                    tooltip={tooltip}
                    onClick={toggleExpanded}
                />
            }
            <Drawer
                title={title}
                placement="right"
                open={expanded}
                onClose={toggleExpanded}
                width={width}
            >
                <Space direction="vertical" size={16} className="ot-line">
                    {children}
                </Space>
            </Drawer>
        </>
    )
}