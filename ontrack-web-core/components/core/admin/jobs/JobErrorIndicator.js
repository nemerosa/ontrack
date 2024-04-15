import CheckIcon from "@components/common/CheckIcon";
import {Tooltip, Typography} from "antd";
import {FaBan, FaTimesCircle} from "react-icons/fa";

export default function JobErrorIndicator({job}) {
    return (
        <>
            {
                job.lastErrorCount < 1 && <Tooltip title="No error">
                    <Typography.Text type="secondary">
                        <FaBan/>
                    </Typography.Text>
                </Tooltip>
            }
            {
                job.lastErrorCount >= 1 && <Tooltip title={`${job.lastErrorCount} error(s)`}>
                    <Typography.Text type="danger">
                        <FaTimesCircle/>
                    </Typography.Text>
                </Tooltip>
            }
        </>
    )
}