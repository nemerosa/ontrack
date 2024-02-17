import {Space, Tooltip, Typography} from "antd";
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
                    version === latestVersion && <Tooltip title="Using the latest version">
                        <FaCheck color="green"/>
                    </Tooltip>
                }
                {
                    version !== latestVersion && <Tooltip title="Not using the latest version">
                        <FaTimes color="red"/>
                    </Tooltip>
                }
            </Space>
        </Space>
    )
}