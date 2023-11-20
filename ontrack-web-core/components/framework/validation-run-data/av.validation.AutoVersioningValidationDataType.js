import {Space, Typography} from "antd";
import {FaArrowRight, FaCheck, FaTimes} from "react-icons/fa";

export default function AutoVersioningValidationDataType({project, version, latestVersion}) {
    return (
        <Space>
            <b>{project}</b>
            <Space>
                <Typography.Text code>{version}</Typography.Text>
                <FaArrowRight/>
                <Typography.Text code>{latestVersion}</Typography.Text>
                {
                    version === latestVersion && <FaCheck color="green"/>
                }
                {
                    version !== latestVersion && <FaTimes color="red"/>
                }
            </Space>
        </Space>
    )
}