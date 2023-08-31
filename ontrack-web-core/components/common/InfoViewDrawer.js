import {Drawer, FloatButton} from "antd";
import {useState} from "react";
import {FaInfoCircle} from "react-icons/fa";

export default function InfoViewDrawer({title, tooltip, children}) {

    const [expanded, setExpanded] = useState(false)

    const toggleExpanded = () => {
        setExpanded(!expanded)
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
            >
                {children}
            </Drawer>
        </>
    )
}