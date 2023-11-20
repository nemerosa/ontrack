import {useContext} from "react";
import {MainLayoutContext} from "@components/layouts/MainLayout";
import {FloatButton} from "antd";
import {FaCompressArrowsAlt} from "react-icons/fa";

export default function MainLayoutRestoreViewButton() {

    const {expanded, toggleExpansion} = useContext(MainLayoutContext)

    return (
        <>
            {
                expanded && <FloatButton
                    icon={<FaCompressArrowsAlt/>}
                    tooltip="Restore the view"
                    onClick={toggleExpansion}
                />
            }
        </>
    )
}